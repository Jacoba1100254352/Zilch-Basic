package creators.patterns;


import controllers.GameServer;
import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.dispatchers.SimpleEventDispatcher;
import model.managers.*;
import rules.managers.*;
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
		IEventDispatcher dispatcher = new SimpleEventDispatcher();
		IPlayerManager playerManager = new PlayerManager(playerNames);
		IDiceManager diceManager = new DiceManager();
		IRuleRegistry ruleRegistry = new RuleRegistry();
		IRuleManager ruleManager = new RuleManager(ruleRegistry);
		
		// Initialize the rules with the selected configuration
		ruleManager.initializeRules(selectedRules);
		
		ActionManager actionManager = new ActionManager(playerManager, diceManager, ruleManager);
		
		return new GameServer(dispatcher, actionManager, ruleManager, uiManager, scoreLimit, userInteraction, gameID);
	}
}
