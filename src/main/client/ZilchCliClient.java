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
import java.util.Scanner;


public class ZilchCliClient
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
		int openingScoreLimit;

		try (Scanner scanner = new Scanner(System.in)) {
			String mode = args.length > 0 ? args[0] : "writeConfig";

			Config gameConfig = new Config("config.properties");
			GameIDManager gameIDManager = new GameIDManager();
			IUserInteraction userSetup = new UserInteractionManager(scanner);
			IMessage uiManager = new ConsoleMessage(scanner);

			if (mode.equals("readConfig")) {
				numPlayers = gameConfig.getNumPlayers();
				playerNames = gameConfig.getPlayerNames();
				scoreLimit = gameConfig.getScoreLimit();
				openingScoreLimit = gameConfig.getOpeningScoreLimit();

				if (numPlayers != playerNames.size()) {
					System.out.println("Invalid configuration: numPlayers does not match length of playerNames");
					return;
				}
			} else if (mode.equals("writeConfig")) {
				numPlayers = userSetup.getNumberOfPlayers();
				playerNames = userSetup.getPlayerNames(numPlayers);
				scoreLimit = userSetup.getValidScoreLimit();
				openingScoreLimit = userSetup.getValidOpeningScoreLimit(scoreLimit);

				gameConfig.setNumPlayers(numPlayers);
				gameConfig.setPlayerNames(playerNames);
				gameConfig.setScoreLimit(scoreLimit);
				gameConfig.setOpeningScoreLimit(openingScoreLimit);
				gameConfig.saveConfig();
			} else {
				System.out.println("Usage: java ZilchCliClient [readConfig|writeConfig]");
				return;
			}

			String gameID = gameIDManager.generateGameID();
			Map<RuleType, Object> selectedRules = userSetup.selectRules();

			GameServer gameServer = new GameCreator().createSimpleGameServer(
					playerNames,
					uiManager,
					gameID,
					scoreLimit,
					openingScoreLimit,
					userSetup,
					selectedRules
			);

			gameServer.playGame();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
