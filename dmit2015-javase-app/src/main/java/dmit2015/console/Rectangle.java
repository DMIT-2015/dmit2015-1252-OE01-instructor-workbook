package dmit2015.console;

/**
 * This class models an immutable Rectangle shape.
 *
 * @author Sam Wu
 * @version 2026.01.16
 */
public class Rectangle implements Shape {

    // Define readonly fields for length and width
    private final double length;
    private final double width;

    public Rectangle(double length, double width) {
        if (length <= 0 || width <= 0) {
            throw new IllegalArgumentException("Length and Width must be positive non-zero value");
        }
        this.length = length;
        this.width = width;
    }

    public double getLength() {
        return length;
    }

    public double getWidth() {
        return width;
    }

    @Override
    public double getArea() {
        return length * width;
    }

    @Override
    public double getPerimeter() {
        return 2 * (length + width);
    }
}
