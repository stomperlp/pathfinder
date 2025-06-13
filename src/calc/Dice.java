package calc;

import java.util.Random;

/**
 * Represents dice in "NdM" format (e.g. "3d6" for 3 six-sided dice).
 * Provides functionality to parse dice notation and roll dice.
 */
public class Dice 
{
    // Number of dice to roll
    private int numDice;
    // Number of sides per die
    private int numSides;
    // Random number generator for rolling
    Random rand = new Random();

    /**
     * Constructor - parses dice notation string.
     * @param dice String in "NdM" format (e.g. "2d10")
     */
    public Dice(String dice) 
    {
        // Split string on 'd' character
        String[] d = dice.split("d");
        try 
        {
            // Parse number of dice
            this.numDice  = Integer.parseInt(d[0]);
            // Parse number of sides
            this.numSides = Integer.parseInt(d[1]);
        }
        catch (Exception e) 
        {
            // Error handling for invalid format
            System.out.println("Wrong dice syntax");
        }
        // Initialize random number generator
        this.rand = new Random();
    }

    /**
     * Rolls the dice.
     * @return Array with total sum at index 0 and individual rolls in subsequent indices
     */
    public int[] roll() 
    {
        // Result array (index 0 = sum, 1-n = individual rolls)
        int result[] = new int[this.numDice + 1];

        // Roll each die
        for (int i = 0; i < this.numDice; i++) 
        {
            // Random value between 1 and numSides
            result[i + 1] = rand.nextInt(this.numSides) + 1;
            // Add to total sum
            result[0] += result[i + 1];
        }
        return result;
    }

    /**
     * Returns the dice notation string.
     * @return String in "NdM" format (e.g. "3d6")
     */
    public String getDice() 
    {
        return this.numDice + "d" + this.numSides;
	}
}