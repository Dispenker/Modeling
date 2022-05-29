package ru.dispenker.project;

public class Vector {
    public double X;
    public double Y;
    public double Z;

    public Vector(double X, double Y, double Z) {
        this.X = X;
        this.Y = Y;
        this.Z = Z;
    }

    public static Vector getVector(Vector vector1, Vector vector2) {
        return new Vector(vector2.X - vector1.X, vector2.Y - vector1.Y, vector2.Z - vector1.Z);
    }

    public double absoluteValue() {
        return Math.sqrt(X * X + Y * Y + Z * Z);
    }

    public double absoluteSqrValue() {
        return X * X + Y * Y + Z * Z;
    }

    public void add(Vector velocity, Vector acceleration, double step) {
        X += velocity.X * step + acceleration.X * step * step / 2;
        Y += velocity.Y * step + acceleration.Y * step * step / 2;
        Z += velocity.Z * step + acceleration.Z * step * step / 2;
    }

    public void add(Vector acceleration, double step) {
        X += acceleration.X * step;
        Y += acceleration.Y * step;
        Z += acceleration.Z * step;
    }
}
