package ru.dispenker.project;

public class Field {
    public double width;
    public double height;

    public Field (double width, double height) {
        this.width = width;
        this.height = height;
    }

    public double volume() {
        return width * width * height;
    }

    public Vector checkCollision(Vector position) {
        return new Vector(
                (position.X < 0) ? -position.X : (position.X > width) ? 2 * width - position.X : position.X,
                (position.Y < 0) ? -position.Y : (position.Y > width) ? 2 * width - position.Y : position.Y,
                (position.Z < 0) ? -position.Z : (position.Z > height) ? 2 * height - position.Z : position.Z
        );
    }
}
