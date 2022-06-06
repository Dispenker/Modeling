package ru.dispenker.project;

import java.io.FileWriter;
import java.io.IOException;

public class CharacteristicsWriter {

    private String path = "G:\\Projects\\Java\\Modelation\\Tests\\";
    private FileWriter fileWriter;
    private int counter = 0;

    public CharacteristicsWriter(String name) {
        try {
            fileWriter = new FileWriter(path + name);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void writeFile(String text) {
        try {
            fileWriter.write(text + "\n");
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    public void writeFile(Characteristics characteristics) {
        try {
            fileWriter.write("INIT " + ++counter + "  potentialEnergy = " + characteristics.totalPotentialEnergy + "\n");
            fileWriter.write("layer density pressure temperature\n");
        } catch (IOException e) {
            System.out.println(e);
        }

        for (int i = 0; i < characteristics.countLayers; i++) {
            String text = (i + 1) + " " + characteristics.density[i] + " " + characteristics.pressure[i] + " " + characteristics.temperature[i] + "\n";
            try {
                fileWriter.write(text);
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public void close() {
        try {
            fileWriter.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }
}
