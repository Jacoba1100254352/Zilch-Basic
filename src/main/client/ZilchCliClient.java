package client;


import config.Config;
import controllers.GameServer;
import creators.core.GameCreator;
import creators.core.GameIDManager;
import model.entities.PlayerConfiguration;
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
		List<PlayerConfiguration> playerConfigurations;
		int scoreLimit;
		int openingScoreLimit;

		try (Scanner scanner = new Scanner(System.in)) {
			String mode = args.length > 0 ? args[0] : "writeConfig";

			Config gameConfig = new Config("config.properties");
			GameIDManager gameIDManager = new GameIDManager();
			IUserInteraction userSetup = new UserInteractionManager(scanner);

			if (mode.equals("readConfig")) {
				numPlayers = gameConfig.getNumPlayers();
				playerConfigurations = gameConfig.getPlayerConfigurations();
				scoreLimit = gameConfig.getScoreLimit();
				openingScoreLimit = gameConfig.getOpeningScoreLimit();

				if (numPlayers != playerConfigurations.size()) {
					System.out.println("Invalid configuration: numPlayers does not match the configured players");
					return;
				}
			} else if (mode.equals("writeConfig")) {
				numPlayers = userSetup.getNumberOfPlayers();
				playerConfigurations = userSetup.getPlayerConfigurations(numPlayers);
				scoreLimit = userSetup.getValidScoreLimit();
				openingScoreLimit = userSetup.getValidOpeningScoreLimit(scoreLimit);

				gameConfig.setPlayerConfigurations(playerConfigurations);
				gameConfig.setScoreLimit(scoreLimit);
				gameConfig.setOpeningScoreLimit(openingScoreLimit);
				gameConfig.saveConfig();
			} else {
				System.out.println("Usage: java ZilchCliClient [readConfig|writeConfig]");
				return;
			}

			IMessage uiManager = new ConsoleMessage(scanner, scoreLimit);
			String gameID = gameIDManager.generateGameID();
			Map<RuleType, Object> selectedRules = userSetup.selectRules();

			GameServer gameServer = new GameCreator().createConfiguredGameServer(
					playerConfigurations,
					uiManager,
					gameID,
					scoreLimit,
					openingScoreLimit,
					userSetup,
					selectedRules
			);

			gameServer.playGame();
		} catch (IOException exception) {
			exception.printStackTrace();
		}
	}
}
