package ru.dispenker.project;

import java.io.FileWriter;
import java.io.IOException;

public class Characteristics {
    public final int countLayers;

    public double[] density;
    public double[] pressure;
    public double[] temperature;

    double totalDensity = 0;
    double totalPressure = 0;
    double totalTemperature = 0;
    double totalPotentialEnergy = 0;

    public Characteristics(int countLayers) {
        this.countLayers = countLayers;
        density = new double[countLayers];
        pressure = new double[countLayers];
        temperature = new double[countLayers];
    }

    public void addDensity(int index, double value) {
        if (Double.isNaN(value)) {
            return;
        }
        density[index] = value;
        totalDensity += value / countLayers;
    }

    public void addPressure(int index, double value) {
        if (Double.isNaN(value)) {
            return;
        }
        pressure[index] = value;
        totalPressure += value;
    }

    public void addTemperature(int index, double value) {
        if (Double.isNaN(value)) {
            return;
        }
        temperature[index] = value;
        totalTemperature += value / countLayers;
    }
}
