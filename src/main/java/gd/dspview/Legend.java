package gd.dspview;

import java.util.*;

public final class Legend
{
    private final List<LegendEntry> entries;

    public Legend(LegendEntry... entries)
    {
        this.entries = Arrays.asList(entries);
    }

    public List<LegendEntry> getEntries()
    {
        return Collections.unmodifiableList(entries);
    }
}
