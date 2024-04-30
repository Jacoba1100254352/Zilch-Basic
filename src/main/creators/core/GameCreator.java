package creators.core;


import controllers.GameServer;
import creators.patterns.GameBuilder;
import creators.patterns.GameFactory;
import ui.IGameplayUI;

import java.util.List;


@SuppressWarnings("unused")
public class GameCreator
{
	public GameServer createSimpleGameServer(List<String> playerNames, IGameplayUI uiManager, String gameID, int scoreLimit) {
		return new GameFactory().createGameServer(playerNames, uiManager, gameID, scoreLimit);
	}
	
	/*public static GameServer createSimpleGameServer(List<String> playerNames, IGameplayUI uiManager) {
		return GameFactory.createGameServer(playerNames, uiManager, "DefaultGameID", 1000);
	}*/
	
	public GameBuilder newGameServerBuilder() {
		return new GameBuilder();
	}
}
