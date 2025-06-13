package entities;

import calc.AStar;
import calc.Calc;
import fx.*;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import main.GraphicsHandler;

/**
 * Represents a game character entity with size, health, combat stats, and movement properties.
 * Extends the base Entity class with character-specific functionality.
 */
public class Character extends Entity
{
    // Size category constants
    public static final int TINY       = -2;
    public static final int SMALL      = -1;
    public static final int NORMAL     =  0;
    public static final int LARGE      =  1;
    public static final int HUGE       =  2;
    public static final int GARGANTUAN =  3;

    // Character attributes
    private int initiative;  // Turn order priority
    private int maxHealth;   // Maximum health points
    private int attack;      // Attack bonus/damage
    private int health;      // Current health points
    private int speed;       // Movement speed
    private int size;        // Size category
    private int AC;          // Armor Class

    /**
     * Constructs a new Character instance.
     * @param gh GraphicsHandler reference
     * @param image Character's image
     * @param tile Starting hexagon tile
     * @param location Pixel location
     * @param size Size category constant
     * @param maxhealth Maximum health points
     * @param AC Armor Class
     * @param speed Movement speed
     * @param initiative Turn order priority
     */
    public Character(GraphicsHandler gh, Image image, Hexagon tile, Point2D location, int size, int maxhealth, int AC, int speed, int initiative)
    {
        super(gh, Calc.cutCirce(image), tile, location);
        this.size       = size;
        this.maxHealth  = maxhealth;
        this.health     = maxhealth;
        this.attack     = 0;
        this.AC         = AC;
        this.speed      = speed;
        this.initiative = initiative;
    }

    @Override
    public int getSize() {
        return size;
    }
    public void setSize(int s) {
        this.size = s;
    }

    /**
     * Gets all hexagon tiles occupied by this character based on its size.
     * @return List of occupied hexagon tiles
     */
    @Override
    public ArrayList<Hexagon> getOccupiedTiles() {
        occupiedTiles.clear();
        return getOccupiedTiles(tile, size, gh);
    }

    /**
     * Static method to determine occupied tiles for a character at a given position and size.
     * @param h Center hexagon tile
     * @param size Size category
     * @param gh GraphicsHandler reference
     * @return List of occupied hexagon tiles
     */
    public static ArrayList<Hexagon> getOccupiedTiles(Hexagon h, int size, GraphicsHandler gh) {
        ArrayList<Hexagon> occupiedTiles = new ArrayList<>();
        Hexagon[] neighbors = AStar.getNeighbors(h, gh);
        occupiedTiles.add(h);
        switch (size) {
            case TINY       -> {}  // Occupies only center tile
            case SMALL      -> {}  // Occupies only center tile
            case NORMAL     -> {}  // Occupies only center tile
            case LARGE      -> {   // Occupies center + 2 adjacent tiles
                occupiedTiles.add(neighbors[1]);
                occupiedTiles.add(neighbors[2]);
            }
            case HUGE       -> {   // Occupies center + all adjacent tiles
                occupiedTiles.addAll(Arrays.asList(neighbors));
            }
            case GARGANTUAN -> {   // Occupies center + all adjacent + additional outer tiles
                occupiedTiles.addAll(Arrays.asList(neighbors));
                Hexagon[] neighbors2 = AStar.getNeighbors(neighbors[1], gh);
                Hexagon[] neighbors3 = AStar.getNeighbors(neighbors[2], gh);
                occupiedTiles.add(neighbors2[0]);
                occupiedTiles.add(neighbors2[1]);
                occupiedTiles.add(neighbors2[2]);
                occupiedTiles.add(neighbors3[2]);
                occupiedTiles.add(neighbors3[3]);
            }
            default         -> {}
        }
        return occupiedTiles;
    }

    /**
     * Gets the drawing size multiplier based on character size.
     * @return Size multiplier for rendering
     */
    @Override
    public double getDrawSize() {
        return switch (size) {
            case TINY       -> 1;                 // Smallest size
            case SMALL      -> 1.5;               // Slightly larger
            case NORMAL     -> Math.sqrt(3);      // Default size
            case LARGE      -> 2;                // Larger size
            case HUGE       -> 4;                 // Very large
            case GARGANTUAN -> Math.sqrt(3)*5/2 + 1; // Massive size
            default         -> Math.sqrt(3);      // Default fallback
        };
    }

    // Standard getters and setters with no modifications
    public int getMaxHealth() {
        return maxHealth;
    }
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
    }
    public int getHealth() {
        return health;
    }
    public void setHealth(int health) {
        this.health = health;
    }
    public int getAttack() {
        return attack;
    }
    public void setAttack(int attack) {
        this.attack = attack;
    }
    public int getAC() {
        return AC;
    }
    public void setAC(int AC) {
        this.AC = AC;
    }
    @Override
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    public int getInitiative() {
        return initiative;
    }
    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }
}