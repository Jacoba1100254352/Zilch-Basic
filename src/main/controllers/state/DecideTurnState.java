package controllers.state;


import model.managers.ActionManager;
import ui.IUserInteraction;


/**
 * Decides whether the player continues rolling or banks the current round.
 */
public class DecideTurnState implements GameTurnState
{
	private final ActionManager actionManager;
	private final IUserInteraction userInteraction;

	/**
	 * Creates the state that decides whether the turn continues or banks.
	 */
	public DecideTurnState(ActionManager actionManager, IUserInteraction userInteraction) {
		this.actionManager = actionManager;
		this.userInteraction = userInteraction;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		boolean canBankPoints = actionManager.canBankPoints(turnContext.getPlayer());
		boolean rollAgain = userInteraction.shouldRollAgain(turnContext.getPlayer(), canBankPoints);
		if (rollAgain) {
			return GamePhase.ROLL_DICE;
		}

		actionManager.bankCurrentRound(turnContext.getPlayer());
		return GamePhase.END_TURN;
	}
}
