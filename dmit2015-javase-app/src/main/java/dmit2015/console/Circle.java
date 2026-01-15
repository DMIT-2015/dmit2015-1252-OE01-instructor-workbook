package dmit2015.console;

/**
 * This class models a Circle shape.
 *
 * @author Sam Wu
 * @version 2026.01.14
 */
public class Circle {
    // Define a field to track the radius of this circle
    private double radius;

    // Getters/Setters for fields

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        if (radius <= 0) {
            throw new IllegalArgumentException("Radius must be a positive non-zero number");
        }
        this.radius = radius;
    }

    /**
     * Create a new Circle with a radius of 1.0
     */
    public Circle() {
        setRadius(1.0);
    }

    /**
     * Create a new Circle with a specific radius
     * @param radius the radius of the circle
     */
    public Circle(double radius) {
//        this.radius = radius;
        setRadius(radius);
    }

    public double getArea() {
        return Math.PI * radius * radius;
    }

    public double getDiameter() {
        return 2 * radius;
    }

    public double getPerimeter() {
        return 2 * Math.PI * radius;
    }
}
