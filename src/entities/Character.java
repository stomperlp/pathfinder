package entities;

import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Character 
{
    private Point location;
    private int health;
    private int attack;
    private int AC;
    private int speed;
    private int initiative;
    protected ArrayList<Path2D> hexTile = new ArrayList<>();

    public Character(Point location, int health, int attack, int AC, int speed, int initiative)
    {
        this.location = location;
        this.health = health;
        this.attack = attack;
        this.AC = AC;
        this.speed = speed;
        this.initiative = initiative;
    }
}
