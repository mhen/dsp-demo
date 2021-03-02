package gd.graph;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DspEdgeTest {

    @Test
    void getWeight_ExpectsWeightSet()
    {
        var edge = new DspEdge(1.0);
        assertEquals(edge.weight(), 1.0);
    }
}
