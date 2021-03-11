package gd.graphalgorithms;

import gd.graph.*;

import java.util.*;
import java.util.function.*;

public final class DijkstraShortestPaths<V, E>
{
    private final Graph<V, E> graph;
    private final V sourceVertex;
    private final Function<E, Double> costFunction;

    private PriorityQueue<V> priorityQueue;
    private HashMap<V, Double> distanceFromSource;
    private HashMap<V, V> previousNode;
    private HashSet<V> expandedNodes;
    private HashMap<V, VertexState> states;

    public DijkstraShortestPaths(Graph<V, E> graph, V sourceVertex, Function<E, Double> costFunction)
    {
        this.graph = graph;
        this.sourceVertex = sourceVertex;
        this.costFunction = costFunction;
        initialize();
    }

    public Boolean isCompleted()
    {
        return priorityQueue.isEmpty();
    }

    public void step()
    {
        var currentVertex = priorityQueue.poll();
        expandedNodes.add(currentVertex);
        states.put(currentVertex, VertexState.EXPANDED);

        for (var neighbour : graph.getNeighbours(currentVertex))
        {
            if (!expandedNodes.contains(neighbour))
            {
                states.put(neighbour, VertexState.FRONTIER);

                var tentativeDistance = distanceFromSource.get(currentVertex) + costFunction.apply(graph.getEdge(currentVertex, neighbour));
                if (tentativeDistance < distanceFromSource.get(neighbour))
                {
                    distanceFromSource.put(neighbour, tentativeDistance);
                    previousNode.put(neighbour, currentVertex);

                    updatePriority(neighbour);
                }
            }
        }
    }

    public List<V> getShortestPathTo(V targetVertex)
    {
        var path = new LinkedList<V>();
        if (previousNode.get(targetVertex) == null)
        {
            return path;
        }

        path.addFirst(targetVertex);
        while (previousNode.get(path.getFirst()) != null)
        {
            path.addFirst(previousNode.get(path.getFirst()));
        }

        return path;
    }

    public double getShortestPathCostTo(V targetVertex)
    {
        return distanceFromSource.getOrDefault(targetVertex, Double.MAX_VALUE);
    }

    public V getSourceVertex()
    {
        return sourceVertex;
    }

    public Map<V, VertexState> getState()
    {
        return Collections.unmodifiableMap(states);
    }

    private void initialize()
    {
        this.distanceFromSource = new HashMap<>();
        this.priorityQueue = new PriorityQueue<>(Comparator.comparing(distanceFromSource::get));
        this.previousNode = new HashMap<>();
        this.expandedNodes = new HashSet<>();
        this.states = new HashMap<>();

        for (var vertex : graph.getVertices())
        {
            distanceFromSource.put(vertex, Double.MAX_VALUE);
            previousNode.put(vertex, null);
            states.put(vertex, VertexState.PENDING);
        }

        if (sourceVertex != null)
        {
            distanceFromSource.put(sourceVertex, 0.0);
        }

        priorityQueue.addAll(graph.getVertices());
    }

    private void updatePriority(V priorityQueueEntry)
    {
        priorityQueue.remove(priorityQueueEntry);
        priorityQueue.add(priorityQueueEntry);
    }

    public enum VertexState
    {
        PENDING, FRONTIER, EXPANDED
    }
}
