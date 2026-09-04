package creators.patterns;

import controllers.GameServer;
import model.entities.PlayerConfiguration;
import rules.managers.RuleType;
import ui.IMessage;
import ui.IUserInteraction;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class GameFactory extends AbstractGameServerCreator
{
	public GameServer createGameServer(
			List<String> playerNames,
			IMessage uiManager,
			String gameID,
			int scoreLimit,
			IUserInteraction userInteraction,
			Map<RuleType, Object> selectedRules
	) throws IOException {
		return super.createGameServer(
				playerNames, uiManager, gameID, scoreLimit, userInteraction, selectedRules
		);
	}

	public GameServer createGameServer(
			List<String> playerNames,
			IMessage uiManager,
			String gameID,
			int scoreLimit,
			int openingScoreLimit,
			IUserInteraction userInteraction,
			Map<RuleType, Object> selectedRules
	) throws IOException {
		return super.createGameServer(
				playerNames,
				uiManager,
				gameID,
				scoreLimit,
				openingScoreLimit,
				userInteraction,
				selectedRules
		);
	}

	public GameServer createConfiguredGameServer(
			List<PlayerConfiguration> playerConfigurations,
			IMessage uiManager,
			String gameID,
			int scoreLimit,
			int openingScoreLimit,
			IUserInteraction userInteraction,
			Map<RuleType, Object> selectedRules
	) throws IOException {
		return super.createConfiguredGameServer(
				playerConfigurations,
				uiManager,
				gameID,
				scoreLimit,
				openingScoreLimit,
				userInteraction,
				selectedRules
		);
	}
}
