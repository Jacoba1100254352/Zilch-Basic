package creators.core;


import controllers.GameServer;
import creators.patterns.GameBuilder;
import creators.patterns.GameFactory;
import rules.managers.RuleType;
import ui.IMessage;
import ui.IUserInteraction;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public class GameCreator
{
	public GameServer createSimpleGameServer(
			List<String> playerNames, IMessage uiManager, String gameID,
			int scoreLimit, IUserInteraction userInteraction, Map<RuleType, Object> selectedRules
	) throws IOException {
		return new GameFactory().createGameServer(playerNames, uiManager, gameID, scoreLimit, userInteraction, selectedRules);
	}
	
	/*public static GameServer createSimpleGameServer(List<String> playerNames, IMessage uiManager) {
		return GameFactory.createGameServer(playerNames, uiManager, "DefaultGameID", 1000);
	}*/
	
	public GameBuilder newGameServerBuilder() {
		return new GameBuilder();
	}
}
