package ru.dispenker.project;

import java.security.spec.EdECPoint;
import java.util.ArrayList;
import java.util.HashMap;

public class Molecule {

    public final int ID;
    public final Argon gas;

    public Vector position;
    public Vector velocity;
    public Vector acceleration = new Vector(0, 0, 0);

    public double potentialEnergy;
    public double kineticEnergy;

    public double potential = 0;

    private HashMap<Integer, Molecule> nearMolecules;

    public Molecule (final int ID, final Argon gas) {
        this.ID = ID;
        this.gas = gas;
    }

    private void calculateKineticEnergy() {
        kineticEnergy = gas.molarMass * velocity.absoluteValue() * velocity.absoluteValue() / 2;
    }

    public void updatePosition(Field field, double step) {
        position.add(velocity, acceleration, step);
        position = field.checkCollision(position);
    }

    public void updateVelocity(double step) {
        velocity.add(acceleration, step);
        acceleration = new Vector(0, 0, 0);
    }

    public void updateAcceleration(double step) {
        for (Molecule molecule: nearMolecules.values()) {
            Vector vector = Vector.getVector(position, molecule.position);
            double radius = vector.absoluteSqrValue();
            if (radius > Constants.DoubleRc) {
                continue;
            }
            if (radius < Constants.DoubleR0) {
                radius = Constants.DoubleR0;
            }

            double radius3 = radius * radius * radius;
            double radius6 = radius3 * radius3;

            double dPotential = 4 * (1 / radius6 - 1 / radius3);
            potential += dPotential;
            molecule.potential += dPotential;
            molecule.nearMolecules.remove(ID);
        }

        for (Molecule molecule: nearMolecules.values()) {
            Vector vector = Vector.getVector(position, molecule.position);
            acceleration.add(vector, potential);
        }
        potentialEnergy = potential;
        potential = 0;
    }

    public void calculateNearMolecules(ArrayList<Molecule> molecules) {
        nearMolecules = new HashMap<>();
        for (Molecule molecule : molecules) {
            if (ID == molecule.ID) {
                continue;
            }

            Vector vector = Vector.getVector(position, molecule.position);
            double radius = vector.absoluteSqrValue();
            if (radius > Constants.DoubleRm) {
                continue;
            }

            nearMolecules.put(molecule.ID, molecule);
        }
    }
}
