package controllers;


import controllers.state.TurnContext;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import support.TestDoubles;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameEngineTest
{
	@Test
	void processGameTurnDelegatesToStateManagerForCurrentPlayer() throws Exception {
		TrackingGameStateManager gameStateManager = new TrackingGameStateManager();
		Player player = TestDoubles.player("Jacob");
		ActionManager actionManager = new StubActionManager(player, false);

		GameEngine gameEngine = new GameEngine(gameStateManager, actionManager);
		gameEngine.processGameTurn();

		assertSame(player, gameStateManager.capturedTurnContext.getPlayer());
	}

	@Test
	void processGameTurnReturnsEarlyWhenNoCurrentPlayerExists() throws Exception {
		TrackingGameStateManager gameStateManager = new TrackingGameStateManager();
		ActionManager actionManager = new StubActionManager(null, false);

		GameEngine gameEngine = new GameEngine(gameStateManager, actionManager);
		gameEngine.processGameTurn();

		assertNull(gameStateManager.capturedTurnContext);
	}

	@Test
	void isGameOverDelegatesToActionManager() {
		TrackingGameStateManager gameStateManager = new TrackingGameStateManager();
		ActionManager actionManager = new StubActionManager(null, true);

		GameEngine gameEngine = new GameEngine(gameStateManager, actionManager);

		assertTrue(gameEngine.isGameOver());
	}

	private static final class TrackingGameStateManager extends GameStateManager
	{
		private TurnContext capturedTurnContext;

		TrackingGameStateManager() {
			super(
					new GameOptionManager(new RuleManager(new RuleRegistry())),
					new RecordingMessage(),
					new ActionManager(new StubPlayerManager(List.of()), new SequencedDiceManager(), 5000),
					new ScriptedUserInteraction()
			);
		}

		@Override
		public void processTurn(TurnContext turnContext) {
			this.capturedTurnContext = turnContext;
		}
	}

	private static final class StubActionManager extends ActionManager
	{
		private final Player currentPlayer;
		private final boolean gameOver;

		StubActionManager(Player currentPlayer, boolean gameOver) {
			super(
					new StubPlayerManager(currentPlayer == null ? List.of() : List.of(currentPlayer)),
					new SequencedDiceManager(),
					5000
			);
			this.currentPlayer = currentPlayer;
			this.gameOver = gameOver;
		}

		@Override
		public Player getCurrentPlayer() {
			return currentPlayer;
		}

		@Override
		public boolean isGameOver() {
			return gameOver;
		}
	}
}
