package entities;

import fx.*;
import java.awt.Image;
import java.awt.geom.Point2D;
import main.GraphicsHandler;

public class Character extends Entity
{
    private int maxHealth;
    private int health;
    private int attack;
    private int AC;
    private int speed;
    private int initiative;

    public Character(GraphicsHandler gh, Image image, Hexagon tile, Point2D location, int maxhealth, int attack, int AC, int speed, int initiative, int size)
    {
        super(gh, image, tile, location, size);
        this.maxHealth = maxhealth;
        this.health = maxhealth;
        this.attack = attack;
        this.AC = AC;

        this.speed = speed;
        this.initiative = initiative;
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
