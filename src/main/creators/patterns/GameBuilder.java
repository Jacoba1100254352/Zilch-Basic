package creators.patterns;


import controllers.GameServer;
import model.entities.PlayerConfiguration;
import model.managers.ActionManager;
import rules.managers.RuleType;
import ui.IMessage;
import ui.IUserInteraction;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class GameBuilder extends AbstractGameServerCreator
{
	private List<String> playerNames;
	private List<PlayerConfiguration> playerConfigurations;
	private IMessage uiManager;
	private String gameID;
	private int scoreLimit;
	private int openingScoreLimit;
	private IUserInteraction userInteraction;
	private Map<RuleType, Object> selectedRules;
	
	public GameBuilder() {
		playerNames = null;
		playerConfigurations = null;
		uiManager = null;
		gameID = "DefaultGameID";
		scoreLimit = 5000;
		openingScoreLimit = ActionManager.DEFAULT_OPENING_SCORE_LIMIT;
		userInteraction = null;
		selectedRules = null;
	}
	
	public GameBuilder setPlayerNames(List<String> playerNames) {
		this.playerNames = playerNames;
		this.playerConfigurations = null;
		return this;
	}

	public GameBuilder setPlayerConfigurations(List<PlayerConfiguration> playerConfigurations) {
		this.playerConfigurations = playerConfigurations;
		this.playerNames = null;
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

	public GameBuilder setOpeningScoreLimit(int openingScoreLimit) {
		this.openingScoreLimit = openingScoreLimit;
		return this;
	}
	
	public GameBuilder setUserInteraction(IUserInteraction userInteraction) {
		this.userInteraction = userInteraction;
		return this;
	}
	
	public GameBuilder setSelectedRules(Map<RuleType, Object> selectedRules) {
		this.selectedRules = selectedRules;
		return this;
	}
	
	public GameServer build() throws IOException {
		if ((playerNames == null && playerConfigurations == null) || uiManager == null ||
				userInteraction == null || selectedRules == null) {
			throw new IllegalStateException("Missing required fields for building the GameServer.");
		}
		if (playerConfigurations != null) {
			return createConfiguredGameServer(
					playerConfigurations,
					uiManager,
					gameID,
					scoreLimit,
					openingScoreLimit,
					userInteraction,
					selectedRules
			);
		}
		return createGameServer(
				playerNames,
				uiManager,
				gameID,
				scoreLimit,
				openingScoreLimit,
				userInteraction,
				selectedRules
		);
	}
}
