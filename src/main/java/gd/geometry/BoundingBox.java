package gd.geometry;

import java.util.*;

public final class BoundingBox
{
    private final Vector2D lowerLeft;
    private final Vector2D upperRight;

    public BoundingBox(Vector2D lowerLeft, Vector2D upperRight)
    {
        this.lowerLeft = Objects.requireNonNull(lowerLeft);
        this.upperRight = Objects.requireNonNull(upperRight);
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

        BoundingBox that = (BoundingBox) o;

        if (!Objects.equals(lowerLeft, that.lowerLeft))
        {
            return false;
        }
        return Objects.equals(upperRight, that.upperRight);
    }

    @Override
    public int hashCode()
    {
        int result = lowerLeft.hashCode();
        result = 31 * result + upperRight.hashCode();
        return result;
    }
}
