package creators.patterns;

import controllers.GameServer;
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
}
