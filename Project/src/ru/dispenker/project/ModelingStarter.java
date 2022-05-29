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
                //screen Data
            }

            if (counter++ == countSteps) {
                System.out.println("end");
                break;
            }

            move(counter);

        }
    }

    private void move(int step) {
        modelingSystem.updatePosition(step);
    }

    public static void main(String[] args) {
        ModelingSystem ms = new ModelingSystem(new Argon(), 1020, new Field(30, 30));
        ms.placeMolecules();
        ModelingStarter modelingStarter = new ModelingStarter(ms, 100_000, 10_000);
        modelingStarter.modeling();
    }
}
