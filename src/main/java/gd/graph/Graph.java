package gd.graph;

import java.util.*;

public final class Graph<V, E>
{
    private final HashMap<V, HashMap<V, E>> adjacencies;

    public Graph()
    {
        adjacencies = new HashMap<>();
    }

    public void addVertex(V vertex)
    {
        requireVertexDoesNotExist(vertex);
        adjacencies.put(vertex, new HashMap<>());
    }

    public void addEdge(V sourceVertex, V targetVertex, E edge)
    {
        Objects.requireNonNull(edge);
        requireEdgeDoesNotExist(sourceVertex, targetVertex);
        adjacencies.get(sourceVertex).put(targetVertex, edge);
    }

    public void removeVertex(V vertex)
    {
        requireVertexExists(vertex);
        adjacencies.remove(vertex);
        adjacencies.forEach((source, neighbours) -> neighbours.remove(vertex));
    }

    public void removeEdge(V sourceVertex, V targetVertex)
    {
        requireEdgeExists(sourceVertex, targetVertex);
        adjacencies.get(sourceVertex).remove(targetVertex);
    }

    public E getEdge(V sourceVertex, V targetVertex)
    {
        requireEdgeExists(sourceVertex, targetVertex);
        return adjacencies.get(sourceVertex).get(targetVertex);
    }

    public Boolean hasEdge(V sourceVertex, V targetVertex)
    {
        requireVertexExists(sourceVertex);
        requireVertexExists(targetVertex);
        return adjacencies.get(sourceVertex).containsKey(targetVertex);
    }
    
    public Set<V> getNeighbours(V vertex)
    {
        requireVertexExists(vertex);
        return Collections.unmodifiableSet(adjacencies.get(vertex).keySet());
    }
    
    public Collection<V> getVertices()
    {
        return Collections.unmodifiableCollection(adjacencies.keySet());
    }

    private void requireVertexExists(V vertex)
    {
        Objects.requireNonNull(vertex);

        if (!adjacencies.containsKey(vertex))
        {
            throw new IllegalArgumentException("Vertex with key " + vertex + " does not exist.");
        }
    }

    private void requireVertexDoesNotExist(V vertex)
    {
        Objects.requireNonNull(vertex);

        if (adjacencies.containsKey(vertex))
        {
            throw new IllegalArgumentException("Vertex with key " + vertex + " already exists.");
        }
    }

    private void requireEdgeDoesNotExist(V sourceVertex, V targetVertex)
    {
        requireVertexExists(sourceVertex);
        requireVertexExists(targetVertex);

        if (adjacencies.get(sourceVertex).containsKey(targetVertex))
        {
            throw new IllegalArgumentException("Edge (" + sourceVertex + ", " + targetVertex + ") already exists.");
        }
    }

    private void requireEdgeExists(V sourceVertex, V targetVertex)
    {
        requireVertexExists(sourceVertex);
        requireVertexExists(targetVertex);

        if (!adjacencies.get(sourceVertex).containsKey(targetVertex))
        {
            throw new IllegalArgumentException("Edge (" + sourceVertex + ", " + targetVertex + ") does not exist.");
        }
    }
}