package controllers;


import controllers.state.TurnContext;
import model.entities.Player;
import model.entities.TurnContinuation;
import org.junit.jupiter.api.Test;
import support.TestDoubles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StealingManagerTest
{
	@Test
	void acceptedContinuationCarriesScoreDiceAndMultipleContext() {
		Player alice = TestDoubles.playerWithDice("Alice", Map.of(2, 4));
		alice.score().setRoundScore(450);
		TurnContext aliceTurn = new TurnContext(alice);
		aliceTurn.getScoredMultiples().put(3, 3);
		StealingManager manager = new StealingManager(true, 1000);

		manager.offerContinuation(aliceTurn);

		Player bob = TestDoubles.player("Bob");
		bob.score().increasePermanentScore(1000);
		TurnContext bobTurn = new TurnContext(bob);
		TurnContinuation accepted = manager.acceptContinuation(bobTurn);

		assertEquals("Alice", accepted.sourcePlayerName());
		assertEquals(450, bob.score().getRoundScore());
		assertEquals(4, bob.dice().getNumDiceInPlay());
		assertTrue(bob.dice().getDiceSetMap().isEmpty());
		assertEquals(Map.of(3, 3), bobTurn.getScoredMultiples());
		assertFalse(manager.hasAvailableContinuation());
	}

	@Test
	void unopenedPlayerCannotUseStealingToReachTheOpeningThreshold() {
		Player alice = TestDoubles.playerWithDice("Alice", Map.of(2, 5));
		alice.score().setRoundScore(950);
		TurnContext aliceTurn = new TurnContext(alice);
		StealingManager manager = new StealingManager(true, 1000);
		manager.offerContinuation(aliceTurn);

		Player bob = TestDoubles.player("Bob");
		bob.score().increasePermanentScore(999);
		TurnContext bobTurn = new TurnContext(bob);

		assertFalse(manager.canSteal(bob));
		assertThrows(IllegalStateException.class, () -> manager.acceptContinuation(bobTurn));

		bob.score().increasePermanentScore(1);
		assertTrue(manager.canSteal(bob));
	}

	@Test
	void sourcePlayerCannotStealTheirOwnPriorTurn() {
		Player alice = TestDoubles.playerWithDice("Alice", Map.of(2, 5));
		alice.score().increasePermanentScore(1000);
		alice.score().setRoundScore(200);
		StealingManager manager = new StealingManager(true, 1000);
		manager.offerContinuation(new TurnContext(alice));

		assertFalse(manager.canSteal(alice));
	}

	@Test
	void partialContinuationCanChainAcrossOpenedPlayers() {
		Player alice = TestDoubles.playerWithDice("Alice", Map.of(1, 3));
		alice.score().setRoundScore(300);
		StealingManager manager = new StealingManager(true, 1000);
		manager.offerContinuation(new TurnContext(alice));

		Player bob = TestDoubles.player("Bob");
		bob.score().increasePermanentScore(1000);
		TurnContext bobTurn = new TurnContext(bob);
		manager.acceptContinuation(bobTurn);
		bob.score().increaseRoundScore(150);
		bob.dice().setNumDiceInPlay(2);
		manager.offerContinuation(bobTurn);

		Player charlie = TestDoubles.player("Charlie");
		charlie.score().increasePermanentScore(1000);
		TurnContext charlieTurn = new TurnContext(charlie);
		TurnContinuation chained = manager.acceptContinuation(charlieTurn);

		assertEquals("Bob", chained.sourcePlayerName());
		assertEquals(450, charlie.score().getRoundScore());
		assertEquals(2, charlie.dice().getNumDiceInPlay());
	}

	@Test
	void bustHotDiceAndDisabledVariantDoNotCreateOffers() {
		Player player = TestDoubles.playerWithDice("Alice", Map.of(2, 5));
		player.score().setRoundScore(250);
		TurnContext turnContext = new TurnContext(player);
		StealingManager manager = new StealingManager(true, 1000);

		manager.offerContinuation(turnContext);
		assertTrue(manager.hasAvailableContinuation());

		turnContext.markBusted();
		manager.offerContinuation(turnContext);
		assertFalse(manager.hasAvailableContinuation());

		TurnContext hotDiceTurn = new TurnContext(TestDoubles.player("Bob"));
		hotDiceTurn.getPlayer().score().setRoundScore(1000);
		manager.offerContinuation(hotDiceTurn);
		assertFalse(manager.hasAvailableContinuation());

		StealingManager disabled = new StealingManager(false, 1000);
		disabled.offerContinuation(new TurnContext(player));
		assertFalse(disabled.hasAvailableContinuation());
	}
}
