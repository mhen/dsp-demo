package gd.graph;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GraphTest {

    private Graph<String, Integer> graph;

    @BeforeEach
    void initGraph()
    {
        graph = new Graph<>();
    }

    @Test
    void addVertex_ExpectsVertexCountIncreased()
    {
        assertTrue(graph.getVertices().isEmpty());
        graph.addVertex("v1");
        assertEquals(graph.getVertices().size(), 1);
    }

    @Test
    void removeVertex_ExpectsVertexCountDecreased()
    {
        graph.addVertex("v1");
        assertEquals(graph.getVertices().size(), 1);
        graph.removeVertex("v1");
        assertEquals(graph.getVertices().size(), 0);
    }

    @Test
    void removeVertex_ExpectsAssociatedEdgesRemoved()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addVertex("v3");

        graph.addEdge("v1", "v2", 0);
        graph.addEdge("v2", "v3", 0);

        assertTrue(graph.hasEdge("v1", "v2"));
        assertTrue(graph.hasEdge("v2", "v3"));

        graph.removeVertex("v2");

        assertFalse(graph.hasVertex("v2"));
        assertFalse(graph.getNeighbours("v1").contains("v2"));
        assertThrows(IllegalArgumentException.class , () -> graph.getNeighbours("v2"));
    }

    @Test
    void addEdge_ExpectsNeighbourAdded()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");

        assertEquals(graph.getNeighbours("v1").size(), 0);
        graph.addEdge("v1", "v2", 0);
        assertEquals(graph.getNeighbours("v1").size(), 1);
        assertTrue(graph.getNeighbours("v1").contains("v2"));
    }

    @Test
    void addEdge_ExpectsHasEdgeTrue()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");

        assertFalse(graph.hasEdge("v1", "v2"));
        graph.addEdge("v1", "v2", 0);
        assertTrue(graph.hasEdge("v1", "v2"));
    }

    @Test
    void removeEdge_ExpectsNeighbourRemoved()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addEdge("v1", "v2", 0);

        assertTrue(graph.getNeighbours("v1").contains("v2"));
        graph.removeEdge("v1", "v2");
        assertFalse(graph.getNeighbours("v1").contains("v2"));
    }

    @Test
    void removeEdge_ExpectsHasEdgeFalse()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addEdge("v1", "v2", 0);

        assertTrue(graph.hasEdge("v1", "v2"));
        graph.removeEdge("v1", "v2");
        assertFalse(graph.hasEdge("v1", "v2"));
    }

    @Test
    void getEdge_ExpectsStoredEdgeData()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addEdge("v1", "v2", 1);

        assertEquals(graph.getEdge("v1", "v2"), 1);

        graph.removeEdge("v1", "v2");
        graph.addEdge("v1", "v2", 2);

        assertEquals(graph.getEdge("v1", "v2"), 2);
    }

    @Test
    void addVertexThatAlreadyExists_ExpectsThrowsException()
    {
        graph.addVertex("v1");
        assertThrows(IllegalArgumentException.class, () -> graph.addVertex("v1"));
    }

    @Test
    void addEdgeThatAlreadyExists_ExpectsThrowsException()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");
        graph.addEdge("v1", "v2", 0);

        assertThrows(IllegalArgumentException.class, () -> graph.addEdge("v1", "v2", 1));
    }

    @Test
    void getEdgeThatDoesNotExist_ExpectsThrowsException()
    {
        graph.addVertex("v1");
        graph.addVertex("v2");
        assertThrows(IllegalArgumentException.class, () -> graph.getEdge("v1", "v2"));
    }
}
