package controllers;


import controllers.state.ApplyOptionState;
import controllers.state.ChooseTurnStartState;
import controllers.state.DecideTurnState;
import controllers.state.EndTurnState;
import controllers.state.EvaluateOptionsState;
import controllers.state.GamePhase;
import controllers.state.GameTurnState;
import controllers.state.RollDiceState;
import controllers.state.SelectOptionState;
import controllers.state.StartTurnState;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import rules.managers.RuleType;
import ui.IMessage;
import ui.IUserInteraction;

import java.util.EnumMap;
import java.util.Map;


/**
 * Concrete turn state machine for Zilch. Each state encapsulates one step of
 * the turn lifecycle so the engine can move through a clear, explicit flow.
 */
public class GameStateManager extends AbstractGameStateManager
{
	public GameStateManager(
			GameOptionManager gameOptionManager,
			IMessage uiManager,
			ActionManager actionManager,
			IUserInteraction userInteraction
	) {
		super(createStates(gameOptionManager, uiManager, actionManager, userInteraction));
	}

	/**
	 * Wires the concrete state objects used during a turn.
	 */
	private static Map<GamePhase, GameTurnState> createStates(
			GameOptionManager gameOptionManager,
			IMessage uiManager,
			ActionManager actionManager,
			IUserInteraction userInteraction
	) {
		Map<GamePhase, GameTurnState> states = new EnumMap<>(GamePhase.class);
		StealingManager stealingManager = new StealingManager(
				gameOptionManager.isRuleActive(RuleType.STEALING),
				actionManager.getOpeningScoreLimit()
		);
		states.put(GamePhase.START_TURN, new StartTurnState(actionManager, gameOptionManager));
		states.put(
				GamePhase.CHOOSE_TURN_START,
				new ChooseTurnStartState(stealingManager, userInteraction)
		);
		states.put(GamePhase.ROLL_DICE, new RollDiceState(actionManager, uiManager));
		states.put(
				GamePhase.EVALUATE_OPTIONS,
				new EvaluateOptionsState(gameOptionManager, uiManager, stealingManager)
		);
		states.put(GamePhase.SELECT_OPTION, new SelectOptionState(gameOptionManager, userInteraction));
		states.put(GamePhase.APPLY_OPTION, new ApplyOptionState(actionManager, gameOptionManager, uiManager));
		states.put(
				GamePhase.DECIDE_TURN,
				new DecideTurnState(actionManager, userInteraction, stealingManager)
		);
		states.put(GamePhase.END_TURN, new EndTurnState(gameOptionManager));
		return states;
	}
}
