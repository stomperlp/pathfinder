package fx;

import java.awt.Color;

public class Theme {
    public final String name;

    public final Color primary;
    public final Color secondary;



    public Theme (String name, Color primary, Color secondary) {
        this.name = name.toLowerCase();
        this.primary = primary;
        this.secondary = secondary;
    }
    @Override 
    public String toString() {
        return name + ": Primary " + Integer.toHexString(primary.getRGB()) + ", Secondary " + Integer.toHexString(secondary.getRGB());
    }
    public String getName() {
        return name;
    }
    public Color getPrimary() {
        return primary;
    }
    public Color getSecondary() {
        return secondary;
    }
}
