package gd.geometry;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class BoundingBoxTest
{
    @Test
    public void newBoundingBox_ExpectsParametersSet()
    {
        var boundingBox = new BoundingBox(
                new Vector2D(0.0, 0.0),
                new Vector2D(1.0, 1.0)
        );

        assertEquals(boundingBox.getLowerLeft(), new Vector2D(0.0, 0.0));
        assertEquals(boundingBox.getUpperRight(), new Vector2D(1.0, 1.0));
    }

    @Test
    public void equalsAndHashCode_ExpectsValueSemantics()
    {
        var boundingBox = new BoundingBox(
                new Vector2D(0.0, 0.0),
                new Vector2D(1.0, 1.0)
        );

        var equalBoundingBox = new BoundingBox(
                new Vector2D(0.0, 0.0),
                new Vector2D(1.0, 1.0)
        );

        var unequalBoundingBox = new BoundingBox(
                new Vector2D(0.5, 0.5),
                new Vector2D(2.0, 2.0)
        );

        assertEquals(boundingBox, boundingBox);
        assertNotEquals(boundingBox, null);
        assertEquals(boundingBox, equalBoundingBox);
        assertNotEquals(boundingBox, unequalBoundingBox);

        assertEquals(boundingBox.hashCode(), equalBoundingBox.hashCode());
    }
}
