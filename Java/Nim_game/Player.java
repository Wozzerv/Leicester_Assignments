package ca562;

public class Player {

	// Data members
	private String name;
	private String history;
	
	/*
	 * Constructor for a player object.
	 */
	public Player () {
		
		name = "Unassigned";
		history = "Unassigned";
	}
	/*
	 * This method returns the name of a player object.
	 */
	public String getName () {
		
		return name;
	}
	/*
	 * This method allows us to set the name of a player object.
	 */
	public void setName (String myInput) {
		
		name = myInput;
	}
	/*
	 * This method sets the history of a player.
	 */
	public void setHistory (Player p) {
		
		history = ("Recorded history of player -" + p.getName() + "\n");
			
	}
	/*
	 * This method allows us to return the completed history of a player.
	 */
	public String getHistory () {
		
		return history;
	}
	/*
	 * This method allows us to add to the history of a player.
	 */
	public void addHistory (String add) {
		
		history += add;
	}
}
