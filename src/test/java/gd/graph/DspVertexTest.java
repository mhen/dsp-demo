package gd.graph;

import gd.geometry.Vector2D;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DspVertexTest {

    @Test
    void nullKey_ExpectsThrowsException()
    {
        assertThrows(
                NullPointerException.class,
                () -> new DspVertex(null, new Vector2D(0.0, 0.0), "label")
        );
    }

    @Test
    void newVertex_ExpectsParametersSet()
    {
        var v = new DspVertex("key", new Vector2D(0.0, 0.0), "label");
        assertEquals(v.key(), "key");
        assertEquals(v.position().getX(), 0.0);
        assertEquals(v.position().getY(), 0.0);
        assertEquals(v.label(), "label");
    }

    @Test
    void sameKeyDifferentPosAndLabel_ExpectsSameHashCode()
    {
        var v1 = new DspVertex("key", new Vector2D(0.0, 0.0), "label");
        var v2 = new DspVertex("key", new Vector2D(1.0, 0.0), "differentLabel");
        assertEquals(v1.hashCode(), v2.hashCode());
    }

    @Test
    void differentKeySamePosAndLabel_ExpectsDifferentHashCodes()
    {
        var v1 = new DspVertex("key", new Vector2D(0.0, 0.0), "label");
        var v2 = new DspVertex("DifferentKey", new Vector2D(0.0, 0.0), "label");
        assertNotEquals(v1.hashCode(), v2.hashCode());
    }
    @Test
    void sameKeyDifferentPosAndLabel_ExpectsEqual()
    {
        var v1 = new DspVertex("key", new Vector2D(0.0, 0.0), "label");
        var v2 = new DspVertex("key", new Vector2D(1.0, 0.0), "differentLabel");
        assertEquals(v1, v2);
    }

    @Test
    void differentKeySamePosAndLabel_ExpectsNotEqual()
    {
        var v1 = new DspVertex("key", new Vector2D(0.0, 0.0), "label");
        var v2 = new DspVertex("DifferentKey", new Vector2D(0.0, 0.0), "label");
        assertNotEquals(v1, v2);
    }

}
