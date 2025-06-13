package fx;

import java.awt.Color;

/**
 * Represents a color theme with primary and secondary colors.
 * Provides a simple way to manage and access color schemes throughout the application.
 */
public class Theme {
    // Name of the theme (stored in lowercase)
    public final String name;

    // Primary color of the theme
    public final Color primary;
    // Secondary color of the theme
    public final Color secondary;

    /**
     * Constructs a new Theme with specified colors.
     * @param name The name of the theme (will be converted to lowercase)
     * @param primary The primary color
     * @param secondary The secondary color
     */
    public Theme(String name, Color primary, Color secondary) {
        this.name = name.toLowerCase();
        this.primary = primary;
        this.secondary = secondary;
    }

    /**
     * Returns a string representation of the theme.
     * Format: "name: Primary hexColor, Secondary hexColor"
     * @return String representation of the theme
     */
    @Override 
    public String toString() {
        return name + ": Primary " + Integer.toHexString(primary.getRGB()) + 
               ", Secondary " + Integer.toHexString(secondary.getRGB());
    }

    /**
     * Gets the theme name.
     * @return The name of the theme (in lowercase)
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the primary color.
     * @return The primary Color object
     */
    public Color getPrimary() {
        return primary;
    }

    /**
     * Gets the secondary color.
     * @return The secondary Color object
     */
    public Color getSecondary() {
        return secondary;
    }
}