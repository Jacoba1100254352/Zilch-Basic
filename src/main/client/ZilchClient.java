package client;

import config.Config;
import config.ReadOnlyConfig;
import controllers.GameServer;
import creators.core.GameCreator;
import creators.core.GameIDManager;
import rules.managers.RuleType;
import ui.ConsoleMessage;
import ui.IMessage;
import ui.IUserInteraction;
import ui.UserInteractionManager;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class ZilchClient
{
	public static void main(String[] args) {
		args = new String[] {"writeConfig"}; // Hardcoded for testing
		
		int numPlayers;
		List<String> playerNames;
		int scoreLimit;
		
		try {
			Config config = new Config("config.properties");
			GameIDManager gameIDManager = new GameIDManager();
			IUserInteraction userSetup = new UserInteractionManager();
			IMessage uiManager = new ConsoleMessage();
			
			if (args.length > 0 && args[0].equals("readConfig")) {
				
				// Use configuration values
				numPlayers = config.getNumPlayers();
				playerNames = config.getPlayerNames();
				scoreLimit = config.getScoreLimit();
				
				if (numPlayers != playerNames.size()) {
					System.out.println("Invalid configuration: numPlayers does not match length of playerNames");
					return;
				}
			} else if (args.length > 0 && args[0].equals("writeConfig")) {
				// Get user input
				numPlayers = userSetup.getNumberOfPlayers();
				playerNames = userSetup.getPlayerNames(numPlayers);
				scoreLimit = userSetup.getValidScoreLimit();
				
				// Set configuration values
				config.setNumPlayers(numPlayers);
				config.setPlayerNames(playerNames);
				config.setScoreLimit(scoreLimit);
				
				// Save configuration to file
				config.saveConfig();
				
				System.out.println("Configuration saved to config.properties");
			} else {
				System.out.println("Usage: java ZilchClient [readConfig|writeConfig]");
				return;
			}
			
			// Generate unique gameID
			String gameID = gameIDManager.generateGameID();
			
			// Get user input for rules
			Map<RuleType, Object> selectedRules = userSetup.selectRules();
			
			// Create game server with the selected rules
			GameServer gameServer = new GameCreator().createSimpleGameServer(
					playerNames, uiManager, gameID, scoreLimit, userSetup, selectedRules
			);
			
			// Play Game
			gameServer.playGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
