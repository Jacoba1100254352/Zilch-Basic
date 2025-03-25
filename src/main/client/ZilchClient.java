package client;


import managers.GameCoordinator;
import ui.ConsoleUserInputHandler;


public class ZilchClient
{
	public static void main(String[] args) {
		// Create an instance of the GameCoordinator class
		GameCoordinator gameCoordinator = new GameCoordinator();
		
		// Attach the real console-based user input
		gameCoordinator.setUserInputHandler(new ConsoleUserInputHandler(gameCoordinator));
		
		// Set up the game
		gameCoordinator.setupGame();
		
		// Start the game
		gameCoordinator.playGame();
	}
}
