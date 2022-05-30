package ru.dispenker.project;

public class ValueConverter {
    public static final double M = 66.336E-27;
    public static final double EPS = 1.65324E-21;
    public static final double GAMMA = 3.405E-10;
    public static final double PRESSURE = EPS / (GAMMA * GAMMA * GAMMA);
    public static final double KB = 1.380649E-23;
    public static final double TEMPERATURE = EPS / KB;

    public static String getTemperature(double temperature) {
        return "Температура: " + temperature * TEMPERATURE + " К";
    }

    public static String getPressure(double pressure) {
        return "Давление: " + pressure * PRESSURE + " ";
    }

    public static String getEnergy(double energy) {
        return "Потенциальная энергия: " + energy * EPS + " Дж";
    }
}
