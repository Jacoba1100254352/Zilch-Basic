package controllers.state;


import model.managers.GameOptionManager;


/**
 * Performs the final cleanup needed before control returns to the server loop.
 */
public class EndTurnState implements GameTurnState
{
	private final GameOptionManager gameOptionManager;

	/**
	 * Creates the terminal cleanup state for a turn.
	 */
	public EndTurnState(GameOptionManager gameOptionManager) {
		this.gameOptionManager = gameOptionManager;
	}

	/** {@inheritDoc} */
	@Override
	public GamePhase handle(TurnContext turnContext) {
		gameOptionManager.setSelectedGameOption(null);
		turnContext.setSelectedOption(null);
		turnContext.getScoredMultiples().clear();
		return GamePhase.END_TURN;
	}
}
