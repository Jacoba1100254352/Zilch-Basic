package client;


import controllers.GameServer;
import eventHandling.dispatchers.SimpleEventDispatcher;
import model.managers.*;
import rules.managers.IRuleManager;
import rules.managers.IRuleRegistry;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import ui.*;

import java.util.List;


public class ZilchClient
{
	public static void main(String[] args) {
		// Input manager for getting user configurations
		IGameplayUI ui = new ConsoleGameplayUI();
		IInputManager inputManager = new ConsoleInputManager();
		IUserInteraction userSetup = new UserInteractionManager(ui, inputManager); // Used for setting up the game
		IGameplayUI uiManager = (IGameplayUI) userSetup; // Used for game interactions
		
		int numPlayers = userSetup.getNumberOfPlayers();
		List<String> playerNames = userSetup.getPlayerNames(numPlayers);
		int scoreLimit = userSetup.getValidScoreLimit();
		
		// TODO: Create function to generate gameID
		String gameID = "Zilch";
		
		// Assuming we have gathered all necessary configurations, we now instantiate game classes
		SimpleEventDispatcher dispatcher = new SimpleEventDispatcher();
		IPlayerManager playerManager = new PlayerManager(playerNames); // Modify to accept number of players or player details
		IDiceManager diceManager = new DiceManager();
		IScoreManager scoreManager = new ScoreManager(scoreLimit);
		ActionManager actionManager = new ActionManager(playerManager, diceManager, scoreManager);
		IRuleRegistry ruleRegistry = new RuleRegistry();
		IRuleManager ruleManager = new RuleManager(ruleRegistry, gameID);
		
		GameServer gameServer = new GameServer(dispatcher, actionManager, ruleManager, uiManager);
		
		// Set up the game
		gameServer.initialize();
		
		// Play Game
		gameServer.playGame();
	}
}
