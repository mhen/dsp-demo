package gd.geometry;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class Vector2DTest
{
    @Test
    public void newVector_ExpectsParametersSet()
    {
        var vector = new Vector2D(1.0, 2.0);
        assertEquals(vector.getX(), 1.0);
        assertEquals(vector.getY(), 2.0);
    }

    @Test
    public void addVectors_ExpectsVectorSum()
    {
        var vectorA = new Vector2D(2.0, 3.0);
        var vectorB = new Vector2D(3.0, 2.0);
        assertEquals(vectorA.add(vectorB), new Vector2D(5.0, 5.0));
    }

    @Test
    public void subtractVectors_ExpectsVectorComponentsSubtracted()
    {
        var vectorA = new Vector2D(2.0, 3.0);
        var vectorB = new Vector2D(3.0, 2.0);
        assertEquals(vectorA.subtract(vectorB), new Vector2D(-1.0, 1.0));
    }

    @Test
    public void dotVectors_ExpectsVectorProduct()
    {
        var vectorA = new Vector2D(2.0, 3.0);
        var vectorB = new Vector2D(3.0, 2.0);
        assertEquals(vectorA.dot(vectorB), 12.0);
    }

    @Test
    public void equalsAndHashCode_ExpectsValueSemantics()
    {
        var vector = new Vector2D(2.0, 3.0);
        var equalVector = new Vector2D(2.0, 3.0);
        var unequalVector = new Vector2D(3.0, 2.0);

        assertEquals(vector, vector);
        assertNotEquals(vector, null);
        assertEquals(vector, equalVector);
        assertNotEquals(vector, unequalVector);

        assertEquals(vector, equalVector);
    }


    @Test
    public void scaleVector_ExpectsVectorScaledByFactor()
    {
        var vector = new Vector2D(1.0, 2.0);
        assertEquals(vector.scale(2.0), new Vector2D(2.0, 4.0));
    }

    @Test
    public void computeLength_ExpectsRootOfSummedSquares()
    {
        var vector = new Vector2D(3.0, 4.0);
        assertEquals(vector.length(), 5.0);
    }

    @Test
    public void normalizeVector_ExpectsVectorScaledByLength()
    {
        var vector = new Vector2D(3.0, 4.0);
        assertEquals(vector.normalize(), new Vector2D(3.0 / 5.0, 4.0 / 5.0));
    }
}
