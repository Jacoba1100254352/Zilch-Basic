package client;


import dispatchers.SimpleEventDispatcher;
import managers.GameServer;
import modelManagers.PlayerManager;
import ui.ConsoleGameplayUI;


public class ZilchClient
{
	public static void main(String[] args) {
		// Create an instance of the GameServer class
		GameServer gameServer = new GameServer(new SimpleEventDispatcher(), new PlayerManager(), new ConsoleGameplayUI());
		
		// Set up the game
		gameServer.setupGame();
		
		// Start the game
		gameServer.playGame(false);
	}
}
