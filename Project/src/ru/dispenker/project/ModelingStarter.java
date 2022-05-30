package ru.dispenker.project;

public class ModelingStarter {

    private ModelingSystem modelingSystem;
    private int countSteps;
    private int stepToScreen;
    private int stepToUpdateNearMolecules;

    private ModelingStarter (ModelingSystem modelingSystem, int countSteps, int stepToScreen) {
        this.modelingSystem = modelingSystem;
        this.countSteps = countSteps;
        this.stepToScreen = stepToScreen;
        stepToUpdateNearMolecules = (int) ((Constants.Rm - Constants.R0) / modelingSystem.maxVelocity);
    }

    private void modeling() {
        int counter = 1;
        while (true) {
            if (counter % stepToUpdateNearMolecules == 0) {
                modelingSystem.calculateNearMolecules();
            }

            if (counter % stepToScreen == 0) {
                double pE = modelingSystem.sumPotentialEnergy / stepToScreen;
                modelingSystem.sumPotentialEnergy = 0;
                double pressure = modelingSystem.calculatePressure();
                double temp = modelingSystem.calculateTemperature(pressure);
                System.out.println(ValueConverter.getEnergy(pE));
                System.out.println(ValueConverter.getPressure(pressure));
                System.out.println(ValueConverter.getTemperature(temp));
                System.out.println(modelingSystem.countSucceeded * 100 / stepToScreen + "%");
                modelingSystem.countSucceeded = 0;
                System.out.println();
            }

            if (counter++ == countSteps) {
                System.out.println("end");
                break;
            }

            move(0.01d);

        }
    }

    private void move(double step) {
        modelingSystem.updatePosition();
    }

    public static void main(String[] args) {
        ModelingSystem ms = new ModelingSystem(new Argon(), 108, 1, new Field(5, 10));
        ms.placeMolecules();
        ModelingStarter modelingStarter = new ModelingStarter(ms, 10_000_000, 1_000_000);
        modelingStarter.modeling();
    }
}
