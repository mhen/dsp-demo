package gd.graphalgorithms;

import gd.graph.*;

import java.util.*;

public final class DijkstraShortestPaths
{
    private final Graph<DspVertex, DspEdge> graph;
    private final DspVertex sourceVertex;

    private PriorityQueue<DspVertex> priorityQueue;
    private HashMap<DspVertex, Double> distanceFromSource;
    private HashMap<DspVertex, DspVertex> previousNode;
    private HashSet<DspVertex> expandedNodes;
    private HashMap<DspVertex, DspVertexState> states;

    public DijkstraShortestPaths(Graph<DspVertex, DspEdge> graph, DspVertex sourceVertex)
    {
        this.graph = graph;
        this.sourceVertex = sourceVertex;
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
        states.put(currentVertex, DspVertexState.EXPANDED);

        for (var neighbour : graph.getNeighbours(currentVertex))
        {
            if (!expandedNodes.contains(neighbour))
            {
                states.put(neighbour, DspVertexState.FRONTIER);

                var tentativeDistance = distanceFromSource.get(currentVertex) + graph.getEdge(currentVertex, neighbour).weight();
                if (tentativeDistance < distanceFromSource.get(neighbour))
                {
                    distanceFromSource.put(neighbour, tentativeDistance);
                    previousNode.put(neighbour, currentVertex);

                    updatePriority(neighbour);
                }
            }
        }
    }

    public List<DspVertex> getShortestPathTo(DspVertex targetVertex)
    {
        var path = new LinkedList<DspVertex>();
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

    public double getShortestPathCostTo(DspVertex targetVertex)
    {
        if (previousNode.get(targetVertex) == null)
        {
            return Double.MAX_VALUE;
        }

        var currentVertex = targetVertex;
        var previousVertex = previousNode.get(targetVertex);
        double totalCost = 0.0;
        while(previousVertex != null)
        {
            totalCost += graph.getEdge(previousVertex, currentVertex).weight();
            currentVertex = previousVertex;
            previousVertex = previousNode.get(previousVertex);
        }
        return totalCost;
    }

    public DspVertex getSourceVertex()
    {
        return sourceVertex;
    }

    public Map<DspVertex, DspVertexState> getState()
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
            states.put(vertex, DspVertexState.PENDING);
        }

        if (sourceVertex != null)
        {
            distanceFromSource.put(sourceVertex, 0.0);
        }

        priorityQueue.addAll(graph.getVertices());
    }

    private void updatePriority(DspVertex priorityQueueEntry)
    {
        priorityQueue.remove(priorityQueueEntry);
        priorityQueue.add(priorityQueueEntry);
    }
}
