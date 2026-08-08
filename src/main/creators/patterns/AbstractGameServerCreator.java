package creators.patterns;


import controllers.GameServer;
import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.dispatchers.SimpleEventDispatcher;
import model.managers.ActionManager;
import model.managers.DiceManager;
import model.managers.IDiceManager;
import model.managers.IPlayerManager;
import model.managers.PlayerManager;
import rules.managers.IRuleManager;
import rules.managers.IRuleRegistry;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import ui.IMessage;
import ui.IUserInteraction;

import java.io.IOException;
import java.util.List;
import java.util.Map;


public abstract class AbstractGameServerCreator
{
	protected GameServer createGameServer(
			List<String> playerNames,
			IMessage uiManager,
			String gameID,
			int scoreLimit,
			IUserInteraction userInteraction,
			Map<RuleType, Object> selectedRules
	) throws IOException {
		return createGameServer(
				playerNames,
				uiManager,
				gameID,
				scoreLimit,
				ActionManager.DEFAULT_OPENING_SCORE_LIMIT,
				userInteraction,
				selectedRules
		);
	}

	protected GameServer createGameServer(
			List<String> playerNames,
			IMessage uiManager,
			String gameID,
			int scoreLimit,
			int openingScoreLimit,
			IUserInteraction userInteraction,
			Map<RuleType, Object> selectedRules
	) throws IOException {
		IEventDispatcher dispatcher = new SimpleEventDispatcher();
		IPlayerManager playerManager = new PlayerManager(playerNames);
		IDiceManager diceManager = new DiceManager();
		IRuleRegistry ruleRegistry = new RuleRegistry();
		IRuleManager ruleManager = new RuleManager(ruleRegistry);

		ruleManager.initializeRules(selectedRules);

		ActionManager actionManager = new ActionManager(
				playerManager,
				diceManager,
				scoreLimit,
				openingScoreLimit
		);
		return new GameServer(dispatcher, actionManager, ruleManager, uiManager, scoreLimit, userInteraction, gameID);
	}
}
