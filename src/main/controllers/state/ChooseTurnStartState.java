package controllers.state;


import controllers.StealingManager;
import model.entities.TurnContinuation;
import ui.IUserInteraction;


/**
 * Lets an eligible player accept or decline the previous player's partial-turn
 * continuation before any dice are rolled.
 */
public class ChooseTurnStartState implements GameTurnState
{
	private final StealingManager stealingManager;
	private final IUserInteraction userInteraction;

	public ChooseTurnStartState(StealingManager stealingManager, IUserInteraction userInteraction) {
		this.stealingManager = stealingManager;
		this.userInteraction = userInteraction;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		if (!stealingManager.hasAvailableContinuation()) {
			return GamePhase.ROLL_DICE;
		}

		if (!stealingManager.canSteal(turnContext.getPlayer())) {
			stealingManager.clearContinuation();
			return GamePhase.ROLL_DICE;
		}

		TurnContinuation continuation = stealingManager.getAvailableContinuation().orElseThrow();
		if (userInteraction.shouldSteal(turnContext.getPlayer(), continuation)) {
			stealingManager.acceptContinuation(turnContext);
		} else {
			stealingManager.clearContinuation();
		}
		return GamePhase.ROLL_DICE;
	}
}
