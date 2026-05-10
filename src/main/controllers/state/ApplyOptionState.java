package controllers.state;


import model.managers.ActionManager;
import model.managers.GameOptionManager;
import ui.IMessage;


/**
 * Applies the chosen scoring option and handles hot-dice replenishment.
 */
public class ApplyOptionState implements GameTurnState
{
	private final ActionManager actionManager;
	private final GameOptionManager gameOptionManager;
	private final IMessage uiManager;

	/**
	 * Creates the state that applies the chosen option and handles hot-dice flow.
	 */
	public ApplyOptionState(ActionManager actionManager, GameOptionManager gameOptionManager, IMessage uiManager) {
		this.actionManager = actionManager;
		this.gameOptionManager = gameOptionManager;
		this.uiManager = uiManager;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		gameOptionManager.applyGameOption(turnContext.toRuleContext(), turnContext.getSelectedOption());
		if (turnContext.getPlayer().dice().getNumDiceInPlay() == 0) {
			actionManager.replenishAllDice();
			uiManager.displayAndWait("Hot dice! All dice scored. Rolling all six dice again.\n");
		}
		return GamePhase.DECIDE_TURN;
	}
}
