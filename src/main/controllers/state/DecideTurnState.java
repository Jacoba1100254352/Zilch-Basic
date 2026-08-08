package controllers.state;


import controllers.StealingManager;
import model.managers.ActionManager;
import ui.IUserInteraction;


/**
 * Decides whether the player continues rolling or banks the current round.
 */
public class DecideTurnState implements GameTurnState
{
	private final ActionManager actionManager;
	private final IUserInteraction userInteraction;
	private final StealingManager stealingManager;

	/**
	 * Creates the state that decides whether the turn continues or banks.
	 */
	public DecideTurnState(ActionManager actionManager, IUserInteraction userInteraction) {
		this(
				actionManager,
				userInteraction,
				new StealingManager(false, actionManager.getOpeningScoreLimit())
		);
	}

	public DecideTurnState(
			ActionManager actionManager,
			IUserInteraction userInteraction,
			StealingManager stealingManager
	) {
		this.actionManager = actionManager;
		this.userInteraction = userInteraction;
		this.stealingManager = stealingManager;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		boolean canBankPoints = actionManager.canBankPoints(turnContext.getPlayer());
		boolean rollAgain = userInteraction.shouldRollAgain(
				turnContext.getPlayer(),
				canBankPoints,
				actionManager.getOpeningScoreLimit()
		);
		if (rollAgain) {
			return GamePhase.ROLL_DICE;
		}

		stealingManager.offerContinuation(turnContext);
		actionManager.bankCurrentRound(turnContext.getPlayer());
		return GamePhase.END_TURN;
	}
}
