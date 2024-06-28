package creators.patterns;


import controllers.GameServer;
import ui.IMessage;

import java.io.IOException;
import java.util.List;


@SuppressWarnings("unused")
public class GameFactory extends AbstractGameServerCreator
{
	public GameServer createGameServer(List<String> playerNames, IMessage uiManager, String gameID) throws IOException {
		return super.createGameServer(playerNames, uiManager, gameID);
	}
}