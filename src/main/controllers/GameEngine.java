package controllers;


import controllers.state.TurnContext;
import model.entities.Player;
import model.managers.ActionManager;

import java.io.IOException;


/**
 * Thin application service that hands the current player to the turn state
 * machine and exposes the game-over check used by the server loop.
 */
public class GameEngine
{
	private final GameStateManager gameStateManager;
	private final ActionManager actionManager;

	/**
	 * Creates the game engine that delegates per-turn work to the state machine.
	 */
	public GameEngine(GameStateManager gameStateManager, ActionManager actionManager) {
		this.gameStateManager = gameStateManager;
		this.actionManager = actionManager;
	}

	/**
	 * Executes a full turn for the current player.
	 */
	public void processGameTurn() throws IOException {
		Player currentPlayer = actionManager.getCurrentPlayer();
		if (currentPlayer == null) {
			System.out.println("No current player available.");
			return;
		}

		gameStateManager.processTurn(new TurnContext(currentPlayer));
	}

	/**
	 * Returns whether the game has entered its terminal phase.
	 */
	public boolean isGameOver() {
		return actionManager.isGameOver();
	}
}
