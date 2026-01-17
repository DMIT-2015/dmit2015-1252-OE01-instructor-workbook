package dmit2015.console;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CircleTest {

    @Test
    void getArea_whenCircleCreatedWithinSpecificRadius_shouldReturnCorrectArea() {
        // Arrange and Act
        Circle circle = new Circle(5);
        // Assert
        assertEquals(78.54, circle.getArea(), 0.005);
    }

    @Test
    void getDiameter_whenCircleIsCreatedWithinSpecificRadius_shouldReturnCorrectDiameter() {
        // Arrange and Act
        Circle circle = new Circle(5);
        // Assert
        assertEquals(10, circle.getDiameter());
    }

    @Test
    void getPerimeter_whenCircleIsCreatedWithinSpecificRadius_shouldReturnCorrectPerimeter() {
        // Arrange and Act
        Circle circle = new Circle(5);
        // Assert
        assertEquals(31.42, circle.getPerimeter(),0.005);
    }
}