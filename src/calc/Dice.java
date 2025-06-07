package calc;

import java.util.Random;

public class Dice 
{
	private int numDice;
	private int numSides;
	Random rand = new Random();

	public Dice(String dice) 
	{
		String[] d = dice.split("d");
		try 
		{
			this.numDice  = Integer.parseInt(d[0]);
			this.numSides = Integer.parseInt(d[1]);
		}
		catch (Exception e) 
		{
			System.out.println("Wrong dice syntax");
		}
		this.rand = new Random();
	}
	// returns int array with total in [0] and the singe results in the rest
	public int[] roll() 
	{
		int result[] = new int[this.numDice + 1];

		for (int i = 0; i < this.numDice; i++) 
		{
			result[i + 1] = rand.nextInt(this.numSides) + 1;
			result[0] += result[i + 1];
		}
		return result;
	}
	/*
	Returns a string representation of the dice object in the format "NdM", where N is the number of dice and M is the number of sides.//+
 	return A string representation of the dice object in the format "NdM".
	*/
	public String getDice() 
	{
		return this.numDice + "d" + this.numSides;
	}
}
