package controllers.state;


import model.managers.ActionManager;
import model.managers.GameOptionManager;
import ui.IUserInteraction;


/**
 * Resets the player's turn-local state and restores the full dice pool.
 */
public class StartTurnState implements GameTurnState
{
	private final ActionManager actionManager;
	private final GameOptionManager gameOptionManager;
	private final IUserInteraction userInteraction;

	/**
	 * Creates the state that resets the active player's turn-local state.
	 */
	public StartTurnState(ActionManager actionManager, GameOptionManager gameOptionManager) {
		this(actionManager, gameOptionManager, null);
	}

	public StartTurnState(
			ActionManager actionManager,
			GameOptionManager gameOptionManager,
			IUserInteraction userInteraction
	) {
		this.actionManager = actionManager;
		this.gameOptionManager = gameOptionManager;
		this.userInteraction = userInteraction;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		if (userInteraction != null) {
			userInteraction.startTurn(turnContext.getPlayer());
		}
		turnContext.resetForNewTurn();
		turnContext.getPlayer().score().setRoundScore(0);
		turnContext.getPlayer().score().setScoreFromMultiples(0);
		gameOptionManager.setSelectedGameOption(null);
		actionManager.replenishAllDice();
		return GamePhase.CHOOSE_TURN_START;
	}
}
