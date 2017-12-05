package ca562;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * @author gsw7
 *This is the main program which is meant to be run.
 *the Piles and Player classes store the Player and Piles objects.
 *The PrettyOutput class is my attempt at the extra credit.
 */

public class Main {

	// Data operators
	
	//The scanner for all inputs.
	Scanner scanner = new Scanner(System.in);
	// The two player objects.
	Player first, second;
	//The three piles. FIRst, SECOnd, THIrd
	Piles st, nd, rd;
	//the string to hold string based inputs.
	String myInput;
	//The int to hold int based inputs and the int which checks whether victory condition has been met.
	int intInput, pileTrue;
	//the int which tells the game which player actually wins.
	int victorycounter = 0;
	//The string which will be used as base to add to player history.
	String variableHistory;
	//These two booleans are used to mantain while loops and stop errors from data returns.
	boolean keepGoing = true;
	boolean skip;
	//This int acts as a value for case switching in pickCounters()
	int switchCounter;

	/**
	 * This creates a new main object called nim. From it the method start() is
	 * called.
	 */
	public static void main(String[] args) {

		Main nim = new Main();
		nim.start();

	}
	/**
	 * This calls all the other methods in the program which make the program
	 */
	public void start() {

		this.instructions();
		this.startup();
		this.history();
		this.gameInitiate();
		this.lastCall();
		scanner.close();

	}
	/**
	 * This method prints out instructions to the player.
	 */
	public void instructions() {

		System.out
				.println("Welcome to the Nim Game as created by Gregory Warren.");
		System.out
				.println("There are three piles and each player can take out any number of counters on their turn.");
		System.out
				.println("Once there are no counters in any of the piles the last player to move is the winner.");

	}
	/**
	 * This method gets input from the user on the names of the player.
	 */
	public void startup() {

		/* Create a new player object "first" and scan for the input with
		 *scanner.
		 *No exception created for user names as any permutation is
		 *permissible.
		 *I am quite partial to anonymous names like Nimplayer69.
		 *Set player name by calling method setName in Player class.*/
		first = new Player();
		System.out.print("Enter the name of the first player: ");
		myInput = scanner.nextLine();
		first.setName(myInput);

		// second player (same method as above)
		second = new Player();
		System.out.print("Enter the name of the second player: ");
		myInput = scanner.nextLine();
		second.setName(myInput);

		// Confirming names

		System.out.print("The name of the first player is: ");
		System.out.print(first.getName() + "\n");

		System.out.print("The name of the second player is: ");
		System.out.print(second.getName() + "\n");

		// creating pile objects and then calling settingPiles() to assign held value.
		st = new Piles("A");
		nd = new Piles("B");
		rd = new Piles("C");
		this.settingPiles(st);
		this.settingPiles(nd);
		this.settingPiles(rd);
	}
	/**
	 * This method is for receiving input on pile sizes.
	 */
	public void settingPiles(Piles p) {
		while (keepGoing) {
			System.out.print("Enter a positive number for tokens in pile "
					+ p.getpileName() + ": ");
			// the keepGoing boolean will keep repeating the loop until
			// keepGoing becomes and remains false
			try {
				intInput = scanner.nextInt();
				keepGoing = false;
			}
			// if intInput is a String the scanner is cleared as otherwise an
			// infinite loop occurs.
			catch (InputMismatchException s) {
				scanner.next();
			}
			// If either the scanner is cleared or the input is negative print
			// error and reset boolean and thus loop.
			if (intInput <= 0) {
				System.out.println("Invalid Input.");
				keepGoing = true;
			}

		}
		p.setheldValue(intInput);
		keepGoing = true;
		intInput = 0;
	}
	/**
	 * This method is the actual game loop.
	 */
	public void gameInitiate() {

		// print game logo.
		System.out.print("============= Start the Game =============\n");

		// the condition for the loop is the checkpiles method. See lines 210.
		while (checkPiles()) {
			// Print the current values for each pile.
			this.gameDisplay();
			/*
			 * call the method for picking which pile and within it calls the
			 * pickCounters() which allows choice of how many counters to remove
			 * with the first player as argument.
			 */
			this.pickPile(first);
			// reset values for loop function.
			keepGoing = true;
			skip = false;
			// call the function which will add to the history based on prior
			// move of first player.
			this.addgameHistory(first);
			// check victory condition. Are all the piles empty. If they are set
			// victorycounter to 1 and break loop.
			this.checkPiles();
			if (!checkPiles()) {
				victorycounter = 1;
				break;
			}
			/*
			 * the second part of the loop is for the second player. Exactly the
			 * same as above but in the point of view of player 2.
			 */
			this.gameDisplay();
			this.pickPile(second);
			keepGoing = true;
			skip = false;
			this.addgameHistory(second);
			this.checkPiles();
			if (!checkPiles()) {
				victorycounter = 2;
				break;

			}
		}
		/*
		 * once the loop has finished once checkPiles() has returned false and
		 * thus true in the if, print who wins based on victorycounter.
		 */
		if (victorycounter == 1) {

			System.out.println(first.getName() + " is the winner!");

		} else if (victorycounter == 2) {

			System.out.println(second.getName() + " is the winner!");

		}

	}
	/**
	 * This method checks whether the victory condition has been met.
	 */
	private boolean checkPiles() {
		//When this method is called pileTrue is reset to 0
		pileTrue = 0;
		/*Check whether first pile has more counters than zero.
		 if it does pileTrue gets one added.*/
		if (st.getheldValue() != 0) {

			pileTrue += 1;

		}
		if (nd.getheldValue() != 0) {

			pileTrue += 1;

		}
		if (rd.getheldValue() != 0) {

			pileTrue += 1;

		}
		//If all above are zero pileTrue will remain unchanged and false is thus returned.
		if (pileTrue == 0) {

			return false;

		} else
			return true;

	}
	/**
	 * This prints out the piles and how much is held n each players turn.
	 */
	private void gameDisplay() {

		System.out.print("\tA:" + st.getheldValue());
		System.out.print("\tB:" + nd.getheldValue());
		System.out.print("\tC:" + rd.getheldValue() + "\n");

	}
	/**
	 * This gets the user to pick which pile they wish to remove stones from.
	 */
	private void pickPile(Player p) {

		System.out.print(p.getName() + ", choose a pile: or enter q to exit. ");
		//Call scanner to wait for input.
		myInput = scanner.next().toUpperCase();
		//switch the input.
		switch (myInput) {
		/*If user selects one of the piles the are taken to the appropriate relevant pickCounters method.
		 *the skip logic exists to make sure that this switch does not keep iterating through
		 the cases once pickCounters has completed its function.*/
		case "A":
			if (skip) {
				break;
			}
			pickCounters(st, p);

		case "B":
			if (skip) {
				break;
			}
			pickCounters(nd, p);

		case "C":
			if (skip) {
				break;
			}
			pickCounters(rd, p);
			//user wrote q to exit the program.
		case "Q":
			if (skip) {
				break;
			}
			System.out.println("You picked q to exit.");
			System.exit(0);
			//catch exceptions case and restart current method.
		default:
			System.out.print("Invalid Input.\n");
			pickPile(p);

		}
	}
	/**
	 *This method adds to game history once a players move is complete. It takes
	 *advantage of the fact that the intInput and input variables have not been 
	 *overwritten to input correct values into the history.
	 */
	private void addgameHistory(Player p) {

		variableHistory = ("\tchosen pile " + myInput + " and removed value["
				+ intInput + "]\n");
		if (p == first) {
			first.addHistory(variableHistory);

		} else
			second.addHistory(variableHistory);

	}
	/**
	 * This method sets the starting sections of the history.
	 */
	private void history() {

		first.setHistory(first);
		second.setHistory(second);
		
	}
	/**
	 * This method retrieves the full history and prints it to the console once the game is complete.
	 */
	private void lastCall() {

		System.out.print(first.getHistory() + "\n");
		System.out.print(second.getHistory());

	}
	/**
	 * This method allows the user to choose how many counters they wish to remove from their selected pile.
	 * It takes arguments Piles and Player to represent the appropriate selected objects to be used.
	 */
	private void pickCounters(Piles c, Player p) {
		
		//reset keepGoing to allow the try and catch to function to catch any non-integer inputs.
		keepGoing = true;
		while (keepGoing) {
			System.out.print("How many counters to remove from pile "
					+ c.getpileName()
					+ ": or enter a negative integer to exit.");
			try {
				intInput = scanner.nextInt();
				keepGoing = false;
				//clear scanner to stop infinite loop in the try above.
			} catch (InputMismatchException s) {
				scanner.next();
				System.out.print("Invalid Input.\n");
			}
		}
		
		/*generate a switch counter for the cases below.
		 * if input is less than 0 make switch equal to 1. 
		 * I interpreted exit for negative numbers as return to pile choice.
		 * instead of exiting the program entirely.
		 * That can be done with q to exit and this allows the user to return and 
		 * choose a new pile if they make a mistake with input.
		 * if input is equal to 0 make switch equal to 3.
		 * if input is greater than the held value of the pile being check make switch equal to 3.
		 * lastly if input is equal or less than the held value but not any of above statements make switch 2.
		 */
		if (intInput < 0) {
			switchCounter = 1;
		} else if (intInput == 0) {
			switchCounter = 3;
		} else if (intInput <= c.getheldValue()) {
			switchCounter = 2;
		} else if (intInput > c.getheldValue()) {
			switchCounter = 3;
		}

		switch (switchCounter) {
		//reset this method.
		default:
			pickCounters(c, p);
			break;
		// go back to choose pile method.
		case 1:
			pickPile(p);
			skip = true;
			break;
		//remove stones as input is valid for removal.
		case 2:
			if (skip) {
				break;
			}
			c.removeStones(intInput);
			skip = true;
			break;
		//this is for invalid input integers. Warn user and reset method.
		case 3:
			System.out.print("Invalid Input.\n");
			pickCounters(c, p);
			break;

		}

	}

}
