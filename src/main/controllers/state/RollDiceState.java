package controllers.state;


import model.managers.ActionManager;
import ui.IMessage;


/**
 * Rolls the dice currently in play and displays the result to the user.
 */
public class RollDiceState implements GameTurnState
{
	private final ActionManager actionManager;
	private final IMessage uiManager;

	/**
	 * Creates the state that rolls and renders the dice currently in play.
	 */
	public RollDiceState(ActionManager actionManager, IMessage uiManager) {
		this.actionManager = actionManager;
		this.uiManager = uiManager;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		uiManager.displayCurrentScore(turnContext.getPlayer().name(), turnContext.getPlayer().score().getRoundScore());
		actionManager.rollDice();
		uiManager.displayDice(turnContext.getPlayer().dice());
		return GamePhase.EVALUATE_OPTIONS;
	}
}
