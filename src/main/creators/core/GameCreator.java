package creators.core;


import controllers.GameServer;
import creators.patterns.GameBuilder;
import creators.patterns.GameFactory;
import ui.IMessage;

import java.io.IOException;
import java.util.List;


@SuppressWarnings("unused")
public class GameCreator
{
	public GameServer createSimpleGameServer(List<String> playerNames, IMessage uiManager, String gameID) throws IOException {
		return new GameFactory().createGameServer(playerNames, uiManager, gameID);
	}
	
	/*public static GameServer createSimpleGameServer(List<String> playerNames, IMessage uiManager) {
		return GameFactory.createGameServer(playerNames, uiManager, "DefaultGameID", 1000);
	}*/
	
	public GameBuilder newGameServerBuilder() {
		return new GameBuilder();
	}
}
