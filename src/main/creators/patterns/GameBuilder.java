package creators.patterns;


import controllers.GameServer;
import ui.IGameplayUI;

import java.util.List;


@SuppressWarnings("unused")
public class GameBuilder extends AbstractGameServerCreator
{
	private List<String> playerNames;
	private IGameplayUI uiManager;
	private String gameID;
	private int scoreLimit;
	
	public GameBuilder() {
		playerNames = null;
		uiManager = null;
		gameID = "DefaultGameID";
		scoreLimit = 1000;
	}
	
	public GameBuilder setPlayerNames(List<String> playerNames) {
		this.playerNames = playerNames;
		return this;
	}
	
	public GameBuilder setUiManager(IGameplayUI uiManager) {
		this.uiManager = uiManager;
		return this;
	}
	
	public GameBuilder setGameID(String gameID) {
		this.gameID = gameID;
		return this;
	}
	
	public GameBuilder setScoreLimit(int scoreLimit) {
		this.scoreLimit = scoreLimit;
		return this;
	}
	
	public GameServer build() {
		return createGameServer(playerNames, uiManager, gameID, scoreLimit);
	}
}