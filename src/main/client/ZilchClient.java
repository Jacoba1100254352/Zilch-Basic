package client;

import config.Config;
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
	/**
	 * Entry point for interactive console play.
	 *
	 * @param args Optional mode selector: {@code readConfig} or {@code writeConfig}.
	 */
	public static void main(String[] args) {
		int numPlayers;
		List<String> playerNames;
		int scoreLimit;

		try {
			String mode = args.length > 0 ? args[0] : "writeConfig";

			Config config = new Config("config.properties");
			GameIDManager gameIDManager = new GameIDManager();
			IUserInteraction userSetup = new UserInteractionManager();
			IMessage uiManager = new ConsoleMessage();

			if (mode.equals("readConfig")) {
				numPlayers = config.getNumPlayers();
				playerNames = config.getPlayerNames();
				scoreLimit = config.getScoreLimit();

				if (numPlayers != playerNames.size()) {
					System.out.println("Invalid configuration: numPlayers does not match length of playerNames");
					return;
				}
			} else if (mode.equals("writeConfig")) {
				numPlayers = userSetup.getNumberOfPlayers();
				playerNames = userSetup.getPlayerNames(numPlayers);
				scoreLimit = userSetup.getValidScoreLimit();

				config.setNumPlayers(numPlayers);
				config.setPlayerNames(playerNames);
				config.setScoreLimit(scoreLimit);
				config.saveConfig();
			} else {
				System.out.println("Usage: java ZilchClient [readConfig|writeConfig]");
				return;
			}

			String gameID = gameIDManager.generateGameID();
			Map<RuleType, Object> selectedRules = userSetup.selectRules();

			GameServer gameServer = new GameCreator().createSimpleGameServer(
					playerNames,
					uiManager,
					gameID,
					scoreLimit,
					userSetup,
					selectedRules
			);

			gameServer.playGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
