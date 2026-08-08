package controllers.state;


import controllers.StealingManager;
import model.entities.Player;
import org.junit.jupiter.api.Test;
import support.TestDoubles;
import support.TestDoubles.ScriptedUserInteraction;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ChooseTurnStartStateTest
{
	@Test
	void eligiblePlayerMayAcceptTheContinuation() {
		StealingManager manager = offeredContinuation();
		Player bob = TestDoubles.player("Bob");
		bob.score().increasePermanentScore(1000);
		TurnContext bobTurn = new TurnContext(bob);
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.addStealingDecision(true);

		GamePhase next = new ChooseTurnStartState(manager, userInteraction).handle(bobTurn);

		assertEquals(GamePhase.ROLL_DICE, next);
		assertEquals(400, bob.score().getRoundScore());
		assertEquals(4, bob.dice().getNumDiceInPlay());
		assertEquals(1, userInteraction.stealingCalls);
		assertFalse(manager.hasAvailableContinuation());
	}

	@Test
	void decliningTheContinuationStartsFresh() {
		StealingManager manager = offeredContinuation();
		Player bob = TestDoubles.player("Bob");
		bob.score().increasePermanentScore(1000);
		TurnContext bobTurn = new TurnContext(bob);
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.addStealingDecision(false);

		GamePhase next = new ChooseTurnStartState(manager, userInteraction).handle(bobTurn);

		assertEquals(GamePhase.ROLL_DICE, next);
		assertEquals(0, bob.score().getRoundScore());
		assertEquals(6, bob.dice().getNumDiceInPlay());
		assertFalse(manager.hasAvailableContinuation());
	}

	@Test
	void unopenedPlayerIsNotPromptedAndTheChainEnds() {
		StealingManager manager = offeredContinuation();
		Player bob = TestDoubles.player("Bob");
		TurnContext bobTurn = new TurnContext(bob);
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.addStealingDecision(true);

		GamePhase next = new ChooseTurnStartState(manager, userInteraction).handle(bobTurn);

		assertEquals(GamePhase.ROLL_DICE, next);
		assertEquals(0, userInteraction.stealingCalls);
		assertTrue(bobTurn.getScoredMultiples().isEmpty());
		assertFalse(manager.hasAvailableContinuation());
	}

	private StealingManager offeredContinuation() {
		Player alice = TestDoubles.playerWithDice("Alice", Map.of(2, 4));
		alice.score().setRoundScore(400);
		StealingManager manager = new StealingManager(true, 1000);
		manager.offerContinuation(new TurnContext(alice));
		return manager;
	}
}
