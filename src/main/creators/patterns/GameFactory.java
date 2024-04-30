package creators.patterns;


import controllers.GameServer;
import ui.IGameplayUI;

import java.util.List;


@SuppressWarnings("unused")
public class GameFactory extends AbstractGameServerCreator
{
	public GameServer createGameServer(List<String> playerNames, IGameplayUI uiManager, String gameID, int scoreLimit) {
		return super.createGameServer(playerNames, uiManager, gameID, scoreLimit);
	}
}