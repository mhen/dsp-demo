package gd.graph;

import gd.geometry.Vector2D;

import java.util.Objects;

public record DspVertex(String key, Vector2D position, String label)
{
    public DspVertex {
        Objects.requireNonNull(key);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DspVertex that = (DspVertex) o;
        return key.equals(that.key);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key);
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "key = '" + key + '\'' +
                ", label = '" + label + '\'' +
                '}';
    }
}
