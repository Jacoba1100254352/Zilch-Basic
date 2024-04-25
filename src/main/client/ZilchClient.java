package client;


import dispatchers.SimpleEventDispatcher;
import managers.GameCoordinator;


public class ZilchClient
{
	public static void main(String[] args) {
		// Create an instance of the GameCoordinator class
		GameCoordinator gameCoordinator = new GameCoordinator(new SimpleEventDispatcher());
		
		// Set up the game
		gameCoordinator.setupGame();
		
		// Start the game
		gameCoordinator.playGame(false);
	}
}
