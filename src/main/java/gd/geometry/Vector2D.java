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
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        Vector2D vector2D = (Vector2D) o;

        if (Double.compare(vector2D.x, x) != 0)
        {
            return false;
        }
        return Double.compare(vector2D.y, y) == 0;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
