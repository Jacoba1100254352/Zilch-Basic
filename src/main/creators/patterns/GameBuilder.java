package creators.patterns;


import controllers.GameServer;
import ui.IMessage;

import java.io.IOException;
import java.util.List;


@SuppressWarnings("unused")
public class GameBuilder extends AbstractGameServerCreator
{
	private List<String> playerNames;
	private IMessage uiManager;
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
	
	public GameBuilder setUiManager(IMessage uiManager) {
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
	
	public GameServer build() throws IOException {
		return createGameServer(playerNames, uiManager, gameID);
	}
}