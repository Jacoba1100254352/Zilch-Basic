package ui;

import managers.GameManager;

import java.util.Scanner;

public class GameplayUI {

    // Display game information
    public void displayGameInfo() {
        clear();
        System.out.println("""
                Welcome to Zilch!

                Here are the basic rules:
                1. You must score an initial 1000 points to start logging your points.
                2. Sets (three sets of two) and straits (1, 2, 3, 4, 5, 6) give 1000 points.
                3. A group of 3 identical dice give you 100 points times the value of that die. For example:
                \t- A roll of 3 3 3 will give you 300 points.
                \t- An exception is made for a roll of 1 1 1, which will give you 1000 points.
                4. Each additional number added to this group of three doubles the points received from it. For example:
                \t- A roll of 3 3 3 3 or a roll of 3 3 3 and a later roll of 3 in the same turn would give you 600 points.
                \t- If in a group of 3 1's, 2 more 1's are rolled, the score would be 1000*2*2 = 4000.
                5. Finally, only single 1's or 5's are worth points with a single 1 being 100 points and a 5 being 50.
                """);
    }

    // Print instructions based on the game state
    public void printInstructions(GameManager game, GameManager.printOptions options) {
        game.getCurrentPlayer().getScore().displayCurrentScore(game.getCurrentPlayer().getName());
        game.getCurrentPlayer().getDice().displayDice();

        String instructions = "\tSingles -- s#, \n\tMultiples -- m#, \n\tSet -- se, \n\tStrait -- st;\n";
        String message = "Enter the option you wish to take";

        if (game.getSelectedOptionStatus() && game.getCurrentPlayer().getScore().getRoundScore() >= 1000) {
            message += ", or type 0 to end your turn: ";
        } else if (game.getSelectedOptionStatus()) {
            message += ", or type 0 to roll again: ";
        } else {
            message += ": ";
        }

        switch (options) {
            case ENTER:
                System.out.print(instructions + message);
                break;
            case NEXT:
                message = message.replaceFirst("Enter", "Please enter your next choice");
                System.out.print(instructions + message);
                break;
            case REENTER:
                message = message.replaceFirst("Enter", "Please re-enter the option you wish to take");
                System.out.print(instructions + message);
                break;
            default:
                throw new IllegalArgumentException("Invalid enumeration, File: " + getClass().getSimpleName());
        }
    }

    // Clear the console
    public void clear() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    // Pause and wait for the user to continue
    public void pauseAndContinue() {
        System.out.print("\nPress enter to continue... ");
        new Scanner(System.in).nextLine(); // Wait for the user to press Enter
        clear();
    }
}
