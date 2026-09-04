package controllers.state;


import model.managers.ActionManager;
import model.managers.GameOptionManager;
import ui.IMessage;
import ui.IUserInteraction;


/**
 * Applies the chosen scoring option and handles hot-dice replenishment.
 */
public class ApplyOptionState implements GameTurnState
{
	private final ActionManager actionManager;
	private final GameOptionManager gameOptionManager;
	private final IMessage uiManager;
	private final IUserInteraction userInteraction;

	/**
	 * Creates the state that applies the chosen option and handles hot-dice flow.
	 */
	public ApplyOptionState(ActionManager actionManager, GameOptionManager gameOptionManager, IMessage uiManager) {
		this(actionManager, gameOptionManager, uiManager, null);
	}

	public ApplyOptionState(
			ActionManager actionManager,
			GameOptionManager gameOptionManager,
			IMessage uiManager,
			IUserInteraction userInteraction
	) {
		this.actionManager = actionManager;
		this.gameOptionManager = gameOptionManager;
		this.uiManager = uiManager;
		this.userInteraction = userInteraction;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		gameOptionManager.applyGameOption(turnContext.toRuleContext(), turnContext.getSelectedOption());
		if (turnContext.getPlayer().dice().getNumDiceInPlay() == 0) {
			actionManager.replenishAllDice();
			turnContext.clearScoredMultiples();
			displayTurnNotice(turnContext, "Hot dice! All dice scored. Rolling all six dice again.\n");
			return GamePhase.DECIDE_TURN;
		}

		gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());
		if (userInteraction != null && !gameOptionManager.getGameOptions().isEmpty() &&
				userInteraction.shouldScoreMore(turnContext.getPlayer(), gameOptionManager.getGameOptions())) {
			turnContext.setSelectedOption(null);
			gameOptionManager.setSelectedGameOption(null);
			return GamePhase.SELECT_OPTION;
		}
		return GamePhase.DECIDE_TURN;
	}

	private void displayTurnNotice(TurnContext turnContext, String message) {
		if (turnContext.getPlayer().isComputer()) {
			uiManager.displayMessage(message);
		} else {
			uiManager.displayAndWait(message);
		}
	}
}
