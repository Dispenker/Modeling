package ru.dispenker.project;

import java.util.Date;

public class ModelingStarter {

    private final CharacteristicsWriter writer;

    private final ModelingSystem modelingSystem;
    private final int countLayers;
    private final int countSteps;
    private final int stepToScreen;
    private final int stepToUpdateNearMolecules;

    private ModelingStarter (ModelingSystem modelingSystem, int countSteps, int stepToScreen, int countLayers) {
        this.modelingSystem = modelingSystem;
        this.countSteps = countSteps;
        this.stepToScreen = stepToScreen;
        this.countLayers = countLayers;
        stepToUpdateNearMolecules = (int) ((Constants.Rm - Constants.R0) / modelingSystem.maxVelocity);
        writer = new CharacteristicsWriter("dump.txt");
    }

    private ModelingStarter (ModelingSystem modelingSystem, int countSteps, int stepToScreen, int countLayers, String file) {
        this.modelingSystem = modelingSystem;
        this.countSteps = countSteps;
        this.stepToScreen = stepToScreen;
        this.countLayers = countLayers;
        stepToUpdateNearMolecules = (int) ((Constants.Rm - Constants.R0) / modelingSystem.maxVelocity);
        writer = new CharacteristicsWriter(file);
    }

    private void modeling() {
        double startTime = new Date().getTime();
        int counter = 1;
        while (true) {
            if (counter % stepToUpdateNearMolecules == 0) {
                modelingSystem.calculateNearMolecules();
            }

            if (counter % stepToScreen == 0) {
                Characteristics characteristics = modelingSystem.calculateCharacteristics(countLayers);

                System.out.println(ValueConverter.getEnergy(characteristics.totalPotentialEnergy));
                System.out.println(ValueConverter.getPressure(characteristics.totalPressure));
                System.out.println(ValueConverter.getTemperature(characteristics.totalTemperature));
                System.out.println(ValueConverter.getDensity(characteristics.totalDensity));
                System.out.println(modelingSystem.countSucceeded * 100 / stepToScreen + "%  " + (new Date().getTime() - startTime) + " ms");
                System.out.println();

                modelingSystem.updateCounters();

                writer.writeFile(characteristics);
            }

            if (counter++ == countSteps) {
                System.out.print("end in " + (new Date().getTime() - startTime) + " ms");
                writer.close();
                break;
            }

            move();
        }
    }

    private void move() {
        modelingSystem.updatePosition();
    }

    public static void main(String[] args) {
        CharacteristicsWriter timeWriter = new CharacteristicsWriter("test1.txt");

        String text = "INIT 1\n";
        ModelingSystem ms = new ModelingSystem(new Argon(), 9482, 1, new Field(16, 38));
        ms.placeMolecules();
        double time = new Date().getTime();
        ModelingStarter modelingStarter = new ModelingStarter(ms, 5_000, 1_000, 900);
        modelingStarter.modeling();
        text += "TIME: " + (new Date().getTime() - time) + "ms\n";
        timeWriter.writeFile(text);

        timeWriter.close();
    }
}
