package client;


import controllers.GameServer;
import creators.core.GameCreator;
import ui.IMessage;
import ui.IUserInteraction;
import ui.UserInteractionManager;

import java.util.List;


public class ZilchClient
{
	public static void main(String[] args) {
		// Configuration Step
		IUserInteraction userSetup = new UserInteractionManager();
		int numPlayers = userSetup.getNumberOfPlayers();
		List<String> playerNames = userSetup.getPlayerNames(numPlayers);
		int scoreLimit = userSetup.getValidScoreLimit();
		
		// Generate gameID
		String gameID = "Zilch"; // TODO: Create function to generate gameID
		
		// Create game server
		GameServer gameServer = new GameCreator().createSimpleGameServer(playerNames, (IMessage) userSetup, gameID, scoreLimit);
		
		// Play Game
		gameServer.playGame();
	}
}
