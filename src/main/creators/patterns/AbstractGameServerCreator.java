package creators.patterns;


import controllers.GameServer;
import eventHandling.dispatchers.IEventDispatcher;
import eventHandling.dispatchers.SimpleEventDispatcher;
import model.managers.*;
import rules.managers.IRuleManager;
import rules.managers.IRuleRegistry;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import ui.IMessage;

import java.util.List;


public abstract class AbstractGameServerCreator
{
	protected GameServer createGameServer(List<String> playerNames, IMessage uiManager, String gameID, int scoreLimit) {
		IEventDispatcher dispatcher = new SimpleEventDispatcher();
		IPlayerManager playerManager = new PlayerManager(playerNames);
		IDiceManager diceManager = new DiceManager();
		IScoreManager scoreManager = new ScoreManager(scoreLimit);
		ActionManager actionManager = new ActionManager(playerManager, diceManager, scoreManager);
		IRuleRegistry ruleRegistry = new RuleRegistry();
		IRuleManager ruleManager = new RuleManager(ruleRegistry, gameID);
		
		return new GameServer(dispatcher, actionManager, ruleManager, uiManager);
	}
}
