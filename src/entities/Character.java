package entities;

import calc.AStar;
import calc.Calc;
import fx.*;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import main.GraphicsHandler;

public class Character extends Entity
{
    public static final int TINY       = -2;
    public static final int SMALL      = -1;
    public static final int NORMAL     =  0;
    public static final int LARGE      =  1;
    public static final int HUGE       =  2;
    public static final int GARGANTUAN =  3;

    private int initiative;
    private int maxHealth;
    private int attack;
    private int health;
    private int speed;
    private int size;
    private int AC;

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
    
    public int getSize() {
        return size;
    }
    public void setSize(int s) {
        this.size = s;
    }

    @Override
    public ArrayList<Hexagon> getOccupiedTiles() {
        occupiedTiles.clear();
        Hexagon[] neighbors = AStar.getNeighbors(tile, gh);
        occupiedTiles.add(tile);
        switch (size) {
            case TINY       -> {}
            case SMALL      -> {}
            case NORMAL     -> {}
            case LARGE      -> {
                occupiedTiles.add(neighbors[1]);
                occupiedTiles.add(neighbors[2]);
            }
            case HUGE       -> {
                occupiedTiles.addAll(Arrays.asList(neighbors));
            }
            case GARGANTUAN -> {
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

    @Override
    public double getDrawSize() {
        return switch (size) {
            case TINY       -> 1;
            case SMALL      -> 1.5;
            case NORMAL     -> Math.sqrt(3);
            case LARGE      -> 2;
            case HUGE       -> 4;
            case GARGANTUAN -> Math.sqrt(3)*5/2 + 1;
            default         -> Math.sqrt(3);
        };
    }
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
