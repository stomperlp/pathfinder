package entities;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.Path2D;
import java.util.ArrayList;

public class Character 
{
    private Image image;
    private Point location;
    private int maxHealth;
    private int health;
    private int attack;
    private int AC;
    private int speed;
    private int initiative;
    protected ArrayList<Path2D> hexTile = new ArrayList<>();

    public Character(Image image, Point location, int maxhealth, int attack, int AC, int speed, int initiative)
    {
        this.image = image;
        this.location = location;
        this.maxHealth = maxhealth;
        this.health = maxhealth;
        this.attack = attack;
        this.AC = AC;
        this.speed = speed;
        this.initiative = initiative;
    }

    public Image getImage() {
        return image;
    }
    public void setImage() {
        this.image = image;
    }
    public Point getLocation() {
        return location;
    }
    public void setLocation(Point location) {
        this.location = location;
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
    public ArrayList<Path2D> getHexTile() {
        return hexTile;
    }
    public void setHexTile(ArrayList<Path2D> hexTile) {
        this.hexTile = hexTile;
    }

}
