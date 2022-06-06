package ru.dispenker.project;

import java.util.ArrayList;

public class ModelingSystem {

    private Field field;
    private final Argon gas;
    private final int mass;
    private double density;
    private double temperature;
    public double maxVelocity;

    public ModelingSystem (Argon gas, int mass, double temperature, Field field) {
        this.gas = gas;
        this.mass = mass;
        this.temperature = temperature;
        density = (mass / gas.weight) / field.volume();
        this.field = field;
        maxVelocity = 0.05 * Constants.R0;
    }

    private ArrayList<Molecule> molecules = new ArrayList<>();

    public void placeMolecules() {
        int countMolecules = (int) Math.round(mass / gas.weight); // Кол-во молекул
        double partCubeInVolume = field.width / field.height;
        double countMoleculesHalfVolume = countMolecules * partCubeInVolume; // Кол-во молекул в половине объема
        int countMoleculesPerUnitW = (int) Math.ceil(Math.pow(countMoleculesHalfVolume, 1d / 3d)); // Кол-во молекул в единице длины
        double countMoleculesPerUnitH = (countMoleculesPerUnitW / partCubeInVolume);

        double freePlace = (field.width - countMoleculesPerUnitW * Constants.R0) / countMoleculesPerUnitW; // Свободное место для расположения частиц
        double freeHeightPlace = (field.height - countMoleculesPerUnitH * Constants.R0) / countMoleculesPerUnitH; // Свободное место для расположения частиц
        if (freePlace < 0 || freeHeightPlace < 0) {
            System.out.println("NO"); return; } // Молекулы не влезут, наверна))

        int counter = 0;

        breakPoint:
        for (int x = 0; x < countMoleculesPerUnitW; x++) {
            for (int y = 0; y < countMoleculesPerUnitW; y++) {
                for (int z = 0; z < countMoleculesPerUnitH; z++) {
                    if (counter++ == countMolecules) {
                        break breakPoint;
                    }

                    Molecule molecule = new Molecule(counter, gas);
                    molecule.position = new Vector(
                            freePlace + (Constants.R0 + freePlace) * x + (RandomUtils.getRandom(0, 1d) * 2 - 1) * freePlace,
                            freePlace + (Constants.R0 + freePlace) * y + (RandomUtils.getRandom(0, 1d) * 2 - 1) * freePlace,
                            freeHeightPlace + (Constants.R0 + freeHeightPlace) * ((x + y) % 2) / 2d + (Constants.R0 + freeHeightPlace) * z + (RandomUtils.getRandom(0, 1d) * 2 - 1) * freeHeightPlace
                    );
                    if (molecule.position.Z > field.height) { continue; }
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

    public void updateCounters() {
        countSucceeded = 0;
        sumPotentialEnergy = 0;
    }

    public void updatePosition() {
        Molecule molecule = molecules.get(RandomUtils.getRandom(0, molecules.size()));
        double maxVelocity = 0.05 * Math.sqrt(molecule.minimumRadius);
        double X = RandomUtils.getRandom(-maxVelocity, maxVelocity);
        double Y = RandomUtils.getRandom(-maxVelocity, maxVelocity);
        double Z = RandomUtils.getRandom(-maxVelocity, maxVelocity);
        Vector dimension = new Vector(X, Y, Z);

        tryMove(molecule, dimension);
    }

    private void tryMove(Molecule molecule, Vector dimension) {
        Molecule movedMolecule = new Molecule(molecule.ID, gas);
        movedMolecule.position = new Vector(molecule.position.X, molecule.position.Y, molecule.position.Z);
        movedMolecule.nearMolecules = molecule.nearMolecules;

        movedMolecule.updatePosition(dimension, field);
        movedMolecule.calculatePotentialEnergy();
        double dEnergy = movedMolecule.potentialEnergy - molecule.potentialEnergy;

        if ((dEnergy > 0) && (RandomUtils.getRandom(0, 1.0) < Math.exp(-dEnergy / (Constants.K * temperature)))) {
            return;
        }

        countSucceeded++;
        molecule.position = movedMolecule.position;
        molecule.potentialEnergy = movedMolecule.potentialEnergy;
        currentPotentialEnergy += dEnergy;
        sumPotentialEnergy += currentPotentialEnergy;
    }

    public Characteristics calculateCharacteristics(int countLayers) {
        double dHeight = field.height / countLayers;
        double nPerMolecule = 1 / (field.width * field.width * dHeight);
        double[] concentrations = new double[countLayers];
        double[] totalVelocity = new double[countLayers];

        for (Molecule molecule: molecules) {
            int layer = (int) (molecule.position.Z / dHeight);
            concentrations[layer]++;
            totalVelocity[layer] += molecule.minimumRadius * 0.0025;
        }

        Characteristics characteristics = new Characteristics(countLayers);
        double pEnergy = sumPotentialEnergy / countSucceeded;
        characteristics.totalPotentialEnergy = pEnergy;

        for (int i = 0; i < countLayers; i++) {
            double nDensity = gas.weight * concentrations[i] * nPerMolecule;
            characteristics.addDensity(i, nDensity);
            double nPressure = nDensity * (totalVelocity[i] / concentrations[i]);
            characteristics.addPressure(i, nPressure);
            double nTemperature = nPressure / (Constants.K * concentrations[i] * nPerMolecule * nPerMolecule);
            characteristics.addTemperature(i, nTemperature);
        }

        return characteristics;
    }
}
