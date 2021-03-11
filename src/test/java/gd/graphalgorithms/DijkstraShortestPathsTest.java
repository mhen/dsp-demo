package gd.graphalgorithms;

import gd.graph.*;
import gd.graphalgorithms.DijkstraShortestPaths.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DijkstraShortestPathsTest
{
    DijkstraShortestPaths<String, Integer> dspInstance;

    @BeforeEach
    public void setupDspInstance()
    {
        Graph<String, Integer> graph = new Graph<>();
        graph.addVertex("A");
        graph.addVertex("B");
        graph.addVertex("C");
        graph.addVertex("D");
        graph.addEdge("A", "B", 2);
        graph.addEdge("A", "C", 2);
        graph.addEdge("B", "D", 1);
        graph.addEdge("C", "D", 2);

        dspInstance = new DijkstraShortestPaths<>(graph, "A", Double::valueOf);
    }

    @Test
    public void newInstance_ExpectsSourceVertexSet()
    {
        assertEquals(dspInstance.getSourceVertex(), "A");
    }

    @Test
    public void runShortestPath_ExpectsShortestPathFound()
    {
        while(!dspInstance.isCompleted())
        {
            dspInstance.step();
        }

        var path = dspInstance.getShortestPathTo("D");

        assertEquals(path.get(0), "A");
        assertEquals(path.get(1), "B");
        assertEquals(path.get(2), "D");

        assertEquals(dspInstance.getShortestPathCostTo("D"), 3.0);
    }

    @Test
    public void queryShortestPathBeforeCompletion_ExpectsEmptyListAndDefaultPathCost()
    {
        assertTrue(dspInstance.getShortestPathTo("D").isEmpty());
        assertEquals(dspInstance.getShortestPathCostTo("D"), Double.MAX_VALUE);
    }

    @Test
    public void queryDspStateDuringExecution_ExpectsConformingVertexStates()
    {
        var initialState = dspInstance.getState();
        assertEquals(VertexState.PENDING, initialState.get("A"));
        assertEquals(VertexState.PENDING, initialState.get("B"));
        assertEquals(VertexState.PENDING, initialState.get("C"));
        assertEquals(VertexState.PENDING, initialState.get("D"));

        dspInstance.step();
        var stepOneState= dspInstance.getState();
        assertEquals(VertexState.EXPANDED, stepOneState.get("A"));
        assertEquals(VertexState.FRONTIER, stepOneState.get("B"));
        assertEquals(VertexState.FRONTIER, stepOneState.get("C"));
        assertEquals(VertexState.PENDING, stepOneState.get("D"));

        dspInstance.step();
        var stepTwoState= dspInstance.getState();
        assertEquals(VertexState.EXPANDED, stepTwoState.get("A"));
        assertEquals(VertexState.EXPANDED, stepTwoState.get("B"));
        assertEquals(VertexState.FRONTIER, stepTwoState.get("C"));
        assertEquals(VertexState.FRONTIER, stepTwoState.get("D"));

        dspInstance.step();
        var stepThreeState= dspInstance.getState();
        assertEquals(VertexState.EXPANDED, stepThreeState.get("A"));
        assertEquals(VertexState.EXPANDED, stepThreeState.get("B"));
        assertEquals(VertexState.EXPANDED, stepThreeState.get("C"));
        assertEquals(VertexState.FRONTIER, stepThreeState.get("D"));

        dspInstance.step();
        var stepFourState= dspInstance.getState();
        assertEquals(VertexState.EXPANDED, stepFourState.get("A"));
        assertEquals(VertexState.EXPANDED, stepFourState.get("B"));
        assertEquals(VertexState.EXPANDED, stepFourState.get("C"));
        assertEquals(VertexState.EXPANDED, stepFourState.get("D"));
    }
}
