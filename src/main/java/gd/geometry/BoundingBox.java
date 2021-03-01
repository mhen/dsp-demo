package gd.geometry;

public final class BoundingBox
{
    private final Vector2D lowerLeft;
    private final Vector2D upperRight;

    public BoundingBox(Vector2D lowerLeft, Vector2D upperRight)
    {
        this.lowerLeft = lowerLeft;
        this.upperRight = upperRight;
    }

    public Vector2D getLowerLeft()
    {
        return lowerLeft;
    }
    public Vector2D getUpperRight()
    {
        return upperRight;
    }

    @Override
    public String toString() {
        return "BoundingBox{lowerLeft = " + lowerLeft + ", upperRight = " + upperRight + '}';
    }
}
