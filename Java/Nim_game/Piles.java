package ca562;

public class Piles {
	
	//Data Members
	
	private String pileName;
	
	private int heldValue;
	
	
	//constructor which creates a pile object. The name of pile created as an argument for the constructor.
	public Piles(String a) {
		pileName = a;
		heldValue = 0;
	}
	/*
	 * This method allows us to set the value of a pile
	 */
	public void setheldValue (int value) {
		
		heldValue = value;
	}
	/*
	 * This method returns the current value of a pile.
	 */
	public int getheldValue () {
		
		return heldValue;
	}
	/*
	 * This method allows to get a name of a pile.
	 */
	public String getpileName () {
		
		return pileName;
	}
	/*
	 * This method removes counters from a pile based on argument applied.
	 */
	public void removeStones(int value) {
		
		heldValue -= value;
	}
}
