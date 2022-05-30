package ru.dispenker.project;

import java.util.ArrayList;
import java.util.HashMap;

public class Molecule {

    public final int ID;
    public final Argon gas;

    public Vector position;

    public double potentialEnergy;

    public HashMap<Integer, Molecule> nearMolecules;

    public Molecule (final int ID, final Argon gas) {
        this.ID = ID;
        this.gas = gas;
    }

    public void updatePosition(Vector dimension, Field field) {
        position = Vector.add(position, dimension);
        position = field.checkCollision(position);
    }

    public void calculatePotentialEnergy() {
        potentialEnergy = 0;
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

            potentialEnergy += 4 * (1 / radius6 - 1 / radius3);
        }
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
