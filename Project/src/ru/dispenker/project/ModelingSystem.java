package ru.dispenker.project;

import java.util.ArrayList;

public class ModelingSystem {

    private Field field;
    private Argon gas;
    private int mass;
    private double density;
    private double temperature;
    public double maxVelocity;

    public ModelingSystem (Argon gas, int mass, double temperature, Field field) {
        this.gas = gas;
        this.mass = mass;
        this.temperature = temperature;
        density = (mass / gas.weight) / field.volume();
        System.out.println(density);
        this.field = field;
        maxVelocity = 0.05 * field.width;
    }

    private ArrayList<Molecule> molecules = new ArrayList<>();

    public void placeMolecules() {
        int countMolecules = (int) Math.round(mass / gas.weight); // Кол-во молекул
        double partCubeInVolume = field.width / field.height;
        int countMoleculesHalfVolume = (int) Math.ceil(countMolecules * partCubeInVolume); // Кол-во молекул в половине объема
        int countMoleculesPerUnitV = (int) Math.ceil(Math.pow(countMoleculesHalfVolume, 1d / 3d)); // Кол-во молекул в единице длины

        double freePlace = (field.width - countMoleculesPerUnitV * Constants.R0) / countMoleculesPerUnitV; // Свободное место для расположения частиц
        if (freePlace < 0) { return; } // Молекулы не влезут, наверна))

        int counter = 0;

        breakPoint:
        for (int x = 0; x < countMoleculesPerUnitV; x++) {
            for (int y = 0; y < countMoleculesPerUnitV; y++) {
                for (int z = 0; z < countMoleculesPerUnitV / (1 - partCubeInVolume); z++) {
                    if (counter++ == countMolecules) {
                        break breakPoint;
                    }

                    Molecule molecule = new Molecule(counter, gas);
                    molecule.position = new Vector(
                            Constants.R0 / 2 + (Constants.R0 + freePlace) * x + RandomUtils.getRandom(0, freePlace),
                            Constants.R0 / 2 + (Constants.R0 + freePlace) * y + RandomUtils.getRandom(0, freePlace),
                            Constants.R0 / 2 + (Constants.R0 + freePlace) * z + RandomUtils.getRandom(0, freePlace)
                    );
                    molecules.add(molecule);
                }
            }
        }

        calculateNearMolecules();
        calculateCurrentPotentialEnergy();
    }

    public void calculateNearMolecules() {
        for (Molecule molecule: molecules) {
            molecule.calculateNearMolecules(molecules);
        }
    }

    public int countSucceeded = 0;
    private double currentPotentialEnergy = 0;
    public double sumPotentialEnergy = 0;

    private void calculateCurrentPotentialEnergy() {
        for (Molecule molecule: molecules) {
            molecule.calculatePotentialEnergy();
            currentPotentialEnergy += molecule.potentialEnergy;
        }
    }

    public void updatePosition() {
        Molecule molecule = molecules.get(RandomUtils.getRandom(0, molecules.size()));
        Vector dimension = new Vector(RandomUtils.getRandom(-maxVelocity, maxVelocity), RandomUtils.getRandom(-maxVelocity, maxVelocity), RandomUtils.getRandom(-maxVelocity, maxVelocity));

        tryMove(molecule, dimension);
    }

    private void tryMove(Molecule molecule, Vector dimension) {
        Molecule movedMolecule = new Molecule(molecule.ID, gas);
        movedMolecule.position = new Vector(molecule.position.X, molecule.position.Y, molecule.position.Z);
        movedMolecule.nearMolecules = molecule.nearMolecules;

        movedMolecule.updatePosition(dimension, field);
        movedMolecule.calculatePotentialEnergy();
        double dEnergy = movedMolecule.potentialEnergy - molecule.potentialEnergy;

        if ((dEnergy > 0) && (RandomUtils.getRandom(0, 1.0) < Math.exp(-dEnergy / temperature))) {
            return;
        }

        countSucceeded++;
        molecule.position = movedMolecule.position;
        molecule.potentialEnergy = movedMolecule.potentialEnergy;
        currentPotentialEnergy += dEnergy;
        sumPotentialEnergy += currentPotentialEnergy;
    }

    public double calculatePressure() {
        return molecules.size() * Constants.K * temperature / (field.volume() * Constants.Na);
    }

    public double calculateTemperature(double pressure) {
        return pressure * field.volume() / (Constants.K * molecules.size());
    }
}
