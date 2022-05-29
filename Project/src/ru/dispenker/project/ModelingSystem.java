package ru.dispenker.project;

import java.util.ArrayList;

public class ModelingSystem {

    private Field field;
    private Argon gas;
    private double mass;
    public double maxVelocity;

    public ModelingSystem (Argon gas, double mass, Field field) {
        this.gas = gas;
        this.mass = mass;
        this.field = field;
        maxVelocity = 1 * Constants.R0;
    }

    private ArrayList<Molecule> molecules = new ArrayList<>();

    public void placeMolecules() {
        int countMolecules = (int) (mass * Constants.Na / gas.molarMass);
        if (!isCanPlaced(countMolecules)) {
            return;
        }

        double amountSubstance = Math.ceil(Math.pow(countMolecules, 1.0 / 3));
        double freePlace = (field.width - amountSubstance * Constants.R0) / amountSubstance;
        int counter = 0;
        breakPoint:
        for (int x = 0; x < amountSubstance; x++) {
            for (int y = 0; y < amountSubstance; y++) {
                for (int z = 0; z < amountSubstance; z++) {
                    if (counter++ == countMolecules) {
                        break breakPoint;
                    }
                    Molecule molecule = new Molecule(counter, gas);
                    molecule.position = new Vector(
                            Constants.R0 / 2 + (Constants.R0 + freePlace) * x + RandomUtils.getRandom(0, freePlace),
                            Constants.R0 / 2 + (Constants.R0 + freePlace) * y + RandomUtils.getRandom(0, freePlace),
                            Constants.R0 / 2 + (Constants.R0 + freePlace) * z + RandomUtils.getRandom(0, freePlace)
                    );
                    molecule.velocity = new Vector(
                            maxVelocity * Math.cos(2 * Math.PI * RandomUtils.getDouble()) * Math.sqrt(-2 * Math.log(RandomUtils.getDouble())),
                            maxVelocity * Math.sin(2 * Math.PI * RandomUtils.getDouble()) * Math.sqrt(-2 * Math.log(RandomUtils.getDouble())),
                            maxVelocity * Math.tan(2 * Math.PI * RandomUtils.getDouble()) * Math.sqrt(-2 * Math.log(RandomUtils.getDouble()))
                    );
                    molecules.add(molecule);
                }
            }
        }

        calculateNearMolecules();
    }

    public void calculateNearMolecules() {
        for (Molecule molecule: molecules) {
            molecule.calculateNearMolecules(molecules);
        }
    }

    private boolean isCanPlaced(int countMolecules) {
        if ((countMolecules * gas.molarMass / Constants.Na) < mass) {
            return true;
        }
        return false;
    }

    public void updatePosition(int step) {
        double pe = 0;
        for (Molecule molecule: molecules) {
            molecule.updatePosition(field, step);
            molecule.updateAcceleration(step);
            molecule.updateVelocity(step);
            pe += molecule.potentialEnergy;
            if (pe > 0) System.out.println(pe);
        }
    }
}
