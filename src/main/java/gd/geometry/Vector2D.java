package gd.geometry;

public final class Vector2D
{
    private final double x;
    private final double y;

    public Vector2D(double x, double y)
    {
        this.x = x;
        this.y = y;
    }

    public Vector2D add(Vector2D otherVector)
    {
        return new Vector2D(x + otherVector.x, y + otherVector.y);
    }

    public Vector2D subtract(Vector2D otherVector)
    {
        return new Vector2D(x - otherVector.x, y - otherVector.y);
    }

    public double dot(Vector2D otherVector)
    {
        return x * otherVector.x + y * otherVector.y;
    }

    public Vector2D scale(double scaleFactor)
    {
        return new Vector2D(x * scaleFactor, y * scaleFactor);
    }

    public double length()
    {
        return Math.sqrt(x * x + y * y);
    }

    public Vector2D normalize()
    {
        var length = length();
        return new Vector2D(x / length, y / length);
    }

    public double getX()
    {
        return x;
    }
    public double getY()
    {
        return y;
    }

    @Override
    public String toString() {
        return "Vector2D{x = " + x + ", y = " + y + '}';
    }
}
