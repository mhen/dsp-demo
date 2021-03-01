package gd.dspview;

import javafx.scene.paint.*;

public final class LegendEntry
{
    private final LegendEntryType type;
    private final String description;
    private final Color color;

    public LegendEntry(LegendEntryType type, Color color, String description)
    {
        this.description = description;
        this.type = type;
        this.color = color;
    }

    public LegendEntryType getType()
    {
        return type;
    }
    public String getDescription()
    {
        return description;
    }
    public Color getColor()
    {
        return color;
    }
}
