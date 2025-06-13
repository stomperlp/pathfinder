package entities;

import calc.Calc;
import fx.Hexagon;
import java.awt.Image;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import main.GraphicsHandler;

/**
 * Represents a wall entity in the game that occupies a single hexagon tile.
 * Extends the base Entity class with wall-specific behavior.
 */
public class Wall extends Entity {

    /**
     * Constructs a new Wall entity.
     * @param gh The GraphicsHandler for rendering
     * @param image The image to use for this wall
     * @param tile The hexagon tile this wall occupies
     * @param location The precise pixel location within the tile
     */
    public Wall(GraphicsHandler gh, Image image, Hexagon tile, Point2D location) {
        // Calls parent constructor with hexagon-cut image
        super(gh, Calc.cutHex(image), tile, location);
    }

    /**
     * Gets all hexagon tiles occupied by this wall.
     * Walls always occupy exactly one tile (their primary tile).
     * @return ArrayList containing only this wall's primary tile
     */
    @Override
    public ArrayList<Hexagon> getOccupiedTiles() {
        // Clear any previous occupied tiles
        occupiedTiles.clear();
        // Add the primary tile
        occupiedTiles.add(tile);
        return occupiedTiles;
    }
}

/*
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢰⡆⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣿⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⣼⠏⠹⣧⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠐⢶⣶⣶⣶⣶⣾⠏⠀⠀⠹⣷⣶⣶⣶⣶⡶⠂⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⣦⣄⠀⠀⠀⠀⠀⠀⠀⣠⣴⠟⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⢙⣷⠀⠀⠀⠀⠀⢾⣏⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⣀⠀⠀⠀⠀⠀⠀⣼⣃⣠⣴⣶⣄⣀⢹⣧⠀⠀⠀⠀⠀⣄⡀⠀⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⢀⣠⣶⠟⠁⠀⠀⠀⠀⢠⣿⠟⠋⠀⠀⠀⠀⠙⢿⣿⡄⠀⠀⠀⠀⠹⣿⣷⣤⠀⠀⠀⠀⠀⠀
⠀⠀⠀⢀⣼⢻⡿⣫⠀⠀⠀⠀⠀⠀⠋⠀⠀⠀⠀⠀⡀⠀⠀⠀⠀⠙⠆⠀⠀⠀⠀⠀⢿⣿⣧⣷⣄⡀⠀⠀
⠀⠀⣰⢹⣟⣽⡾⠃⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠳⣦⣄⡀⠀⠀⠀⠀⠀⠀⠀⠈⢻⣿⣽⡏⢳⡀⠀
⠀⢀⣿⢸⡿⣫⠆⠀⠀⠀⠀⠀⠀⠀⠀⠀⢀⣤⣀⣀⠀⠀⠀⠙⢿⣦⡄⠀⠀⠀⠀⠀⠀⠈⣯⡿⣇⢸⡇⠀
⠀⡜⣿⢸⣿⠏⠀⠀⠀⠀⠀⠀⠀⠀⢀⣴⣿⣿⡿⠋⠀⠀⠀⠀⠈⢻⣿⣦⠀⠀⠀⠀⠀⠀⠘⣿⣮⢸⡟⡄
⢰⡇⢿⣿⢻⠆⠀⠀⠀⠀⠀⠀⠀⢴⣿⣿⡿⣿⣦⡀⠀⠀⠀⠀⠀⠀⢻⣿⣧⠀⠀⠀⠀⠀⠀⣯⢻⣿⢡⡇
⠘⣿⡘⢣⣿⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠋⠀⠈⠻⣿⣦⡀⠀⠀⠀⠀⢸⣿⣿⡄⠀⠀⠀⠀⠀⢹⣧⠛⣾⡇
⢰⢿⣷⣼⡏⡄⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠈⠻⣿⣦⡀⠀⠀⣸⣿⣿⠇⠀⠀⠀⠀⠀⡞⣿⣾⡿⢱
⠘⣧⡻⣿⢰⣧⠀⠀⠀⠀⠀⠀⠀⠀⣠⣄⡀⠀⠀⠀⠀⠈⠻⣿⣦⣠⣿⣿⡿⠀⠀⠀⠀⠀⢰⣇⢸⠟⣰⡏
⠀⠹⣷⣌⢸⣿⢠⡀⠀⠀⠀⠀⢀⣼⡿⠙⠿⣿⣶⣦⣤⣤⣤⣼⣿⣿⣿⡟⠁⠀⠀⠀⠀⣰⢺⡿⣨⣾⠟⠀
⠀⠀⢯⠻⣿⣿⡈⣷⡀⠀⢠⣾⣿⠋⠀⠀⠀⠀⠉⠛⠻⠿⠿⠿⠿⠛⢿⣿⣦⡄⠀⠀⣸⡇⣼⣿⠟⣩⠂⠀
⠀⠀⠈⢿⣦⣙⡷⢻⣷⢦⡈⠛⠁⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠙⠋⠀  ⣠⢶⣿⢑⣋⣴⡾⠋⠀⠀
⠀⠀⠀⠀⠉⡿⢿⣾⣿⣯⢿⣦⡀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀   ⣠⣾⢣⣿⣿⡿⢟⡏⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠈⠳⣶⣬⣭⣥⣽⣿⣦⡤⠄⣀⣀⣀⣀⣀⣀⣀⣀⣀⡤⣴⣾⣿⣥⣭⣤⣶⠾⠋⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠻⣭⣉⣉⣡⣴⣾⡿⠟⣩⡶⠋⠉⠳⣮⣙⠿⣿⣶⣤⣭⣭⣭⠿⠃⠀⠀⠀⠀⠀⠀⠀
⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀⠉⠉⠉⠉⠉⢴⠟⠁⠀⠀⠀⠀⠀⠙⣷⠄⠉⠉⠉⠉⠀⠀⠀⠀⠀⠀⠀⠀⠀⠀
     D݂࣭݂ꪱׅ࣪ᧉ᩠֗ ִׂαׂׅׅ࣭֗ꬻִׂ໋ׅ֗ȶׂׅ݂࣭݂ꪱׅ࣪ƒִִׂ֗αִׂ໋ׅׅ࣪꯱ָׂɕִׄ˖ׅׄჩִׂ݂࣭݂ꪱִ໋ׅ࣪࣪꯱ִָׂ໋֗ȶׂׅ݂࣭݂ꪱִ໋ׅ࣪࣪꯱ָׂɕִׄ˖ׅׄჩִׂᧉ᩠֗ Mִׂαׂׅׅυᧉ᩠֗ꭉׂ໋ׅ ִ໋࣪꯱ָׂɕִׄ˖ׅׄჩִׂüִ໋֗ȶׂׅzִ໋֗ȶׂׅ Bᧉ᩠֗ꭉׂ໋ׅᥣ݂࣭݂ꪱׅ࣭࣪֗ꬻׂׅ
*/