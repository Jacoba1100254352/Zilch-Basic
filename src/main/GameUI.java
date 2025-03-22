// GameUI.java
public class GameUI {
	
	public static void displayGameInfo() {
		clear();
		System.out.println("Welcome to Zilch!\n");
		System.out.println("Here are the basic rules:");
		System.out.println("1. You must score an initial 1000 points to start logging your points.");
		System.out.println("2. Sets (three sets of two) and straits (1, 2, 3, 4, 5, 6) give 1000 points.");
		System.out.println("3. A group of 3 identical dice give you 100 points times the value of that die. For example:");
		System.out.println("\t- A roll of 3 3 3 will give you 300 points.");
		System.out.println("\t- An exception is made for a roll of 1 1 1, which will give you 1000 points.");
		System.out.println("4. Each additional number added to this group of three doubles the points received from it. For example:");
		System.out.println("\t- A roll of 3 3 3 3 or a roll of 3 3 3 and a later roll of 3 in the same turn would give you 600 points.");
		System.out.println("\t- If in a group of 3 1's, 2 more 1's are rolled, the score would be 1000*2*2 = 4000.");
		System.out.println("5. Finally, only single 1's or 5's are worth points with a single 1 being 100 points and a 5 being 50.");
		System.out.println();
	}
	
	public static void printInstructions(GameManager game, GameManager.PrintOptions option) {
		Player currentPlayer = game.getCurrentPlayer();
		currentPlayer.getScore().displayCurrentScore(currentPlayer.getName());
		currentPlayer.getDice().displayDice();
		
		String instructions = "\tSingles -- s#, \n\tMultiples -- m#, \n\tSet -- se, \n\tStrait -- st;\n";
		String message = "Enter the option you wish to take";
		
		if (game.getSelectedOptionStatus() && currentPlayer.getScore().getRoundScore() >= 1000) {
			message += ", or type 0 to end your turn: ";
		} else if (game.getSelectedOptionStatus()) {
			message += ", or type 0 to roll again: ";
		} else {
			message += ": ";
		}
		
		switch (option) {
			case ENTER:
				System.out.print(instructions + message);
				break;
			case NEXT:
				message = message.replace("Enter", "Please enter your next choice");
				System.out.print(instructions + message);
				break;
			case REENTER:
				message = message.replace("Enter", "Please re-enter the option you wish to take");
				System.out.print(instructions + message);
				break;
			default:
				throw new IllegalArgumentException("Invalid print option");
		}
	}
	
	public static void clear() {
		// Clear the console using ANSI escape codes.
		System.out.print("\033[2J\033[1;1H");
	}
	
	public static void pauseAndContinue() {
		System.out.print("\nPress enter to continue... ");
		InputReader.scanner.nextLine();
		clear();
	}
}
