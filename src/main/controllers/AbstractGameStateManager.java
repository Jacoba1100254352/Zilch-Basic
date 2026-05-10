package controllers;


import controllers.state.GamePhase;
import controllers.state.GameTurnState;
import controllers.state.TurnContext;

import java.io.IOException;
import java.util.Map;


/**
 * Base implementation for the turn state machine. Concrete subclasses provide
 * the state map, while this class owns the transition loop.
 */
public abstract class AbstractGameStateManager implements IGameStateManager
{
	private final Map<GamePhase, GameTurnState> states;
	protected GamePhase currentPhase;

	/**
	 * Creates a state manager backed by the supplied phase-to-state map.
	 */
	protected AbstractGameStateManager(Map<GamePhase, GameTurnState> states) {
		this.states = states;
		this.currentPhase = GamePhase.START_TURN;
	}

	@Override
	/**
	 * Returns the phase currently being processed by the turn state machine.
	 */
	public GamePhase getCurrentPhase() {
		return currentPhase;
	}

	@Override
	/**
	 * Runs the current player's turn from {@code START_TURN} until the
	 * terminal {@code END_TURN} phase is reached.
	 */
	public void processTurn(TurnContext turnContext) throws IOException {
		currentPhase = GamePhase.START_TURN;
		while (currentPhase != GamePhase.END_TURN) {
			GameTurnState state = states.get(currentPhase);
			if (state == null) {
				throw new IllegalStateException("No state registered for phase " + currentPhase);
			}
			currentPhase = state.handle(turnContext);
		}

		GameTurnState endTurnState = states.get(GamePhase.END_TURN);
		if (endTurnState != null) {
			endTurnState.handle(turnContext);
		}
	}
}
