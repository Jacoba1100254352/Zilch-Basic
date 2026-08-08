package model.managers;


import model.entities.Player;
import org.junit.jupiter.api.Test;
import support.TestDoubles;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ActionManagerTest
{
	@Test
	void canBankPointsRequiresOpeningThresholdOrOpenedPlayer() {
		Player player = TestDoubles.player("Jacob");
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);

		player.score().setRoundScore(950);
		assertFalse(actionManager.canBankPoints(player));

		player.score().setRoundScore(1000);
		assertTrue(actionManager.canBankPoints(player));

		player.score().setRoundScore(0);
		player.score().increasePermanentScore(1000);
		assertTrue(actionManager.canBankPoints(player));
	}

	@Test
	void openingThresholdIsConfigurableAndOpeningRequiresBankedPoints() {
		Player player = TestDoubles.player("Jacob");
		ActionManager actionManager = new ActionManager(
				new StubPlayerManager(List.of(player)),
				new SequencedDiceManager(),
				5000,
				1500
		);

		player.score().setRoundScore(1499);
		assertFalse(actionManager.canBankPoints(player));
		assertFalse(actionManager.hasOpened(player));

		player.score().setRoundScore(1500);
		assertTrue(actionManager.canBankPoints(player));
		assertFalse(actionManager.hasOpened(player));

		player.score().increasePermanentScore(1500);
		player.score().setRoundScore(0);
		assertTrue(actionManager.hasOpened(player));
		assertTrue(actionManager.canBankPoints(player));
		assertEquals(1500, actionManager.getOpeningScoreLimit());
	}

	@Test
	void bankCurrentRoundMovesPointsAndResetsTurnState() {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(2500);
		player.score().setRoundScore(650);
		player.score().setScoreFromMultiples(600);

		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);
		actionManager.bankCurrentRound(player);

		assertEquals(3150, player.score().getPermanentScore());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(0, player.score().getScoreFromMultiples());
	}

	@Test
	void hasReachedScoreLimitUsesConfiguredLimit() {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(4999);
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);
		assertFalse(actionManager.hasReachedScoreLimit(player));

		player.score().increasePermanentScore(1);
		assertTrue(actionManager.hasReachedScoreLimit(player));
	}

	@Test
	void rollDiceDelegatesToDiceManagerForCurrentPlayersDice() {
		Player player = TestDoubles.player("Jacob");
		StubPlayerManager playerManager = new StubPlayerManager(List.of(player));
		SequencedDiceManager diceManager = new SequencedDiceManager().queueRoll(Map.of(1, 2, 5, 1));

		ActionManager actionManager = new ActionManager(playerManager, diceManager, 5000);
		actionManager.rollDice();

		assertEquals(1, diceManager.rollCalls);
		assertEquals(Map.of(1, 2, 5, 1), player.dice().getDiceSetMap());
		assertEquals(3, player.dice().getNumDiceInPlay());
	}

	@Test
	void replenishAndRemoveDiceMutateCurrentPlayersDice() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(1, 2, 5, 1, 6, 3));
		StubPlayerManager playerManager = new StubPlayerManager(List.of(player));
		SequencedDiceManager diceManager = new SequencedDiceManager();
		ActionManager actionManager = new ActionManager(playerManager, diceManager, 5000);

		actionManager.removeDice(6, 2);
		assertEquals(Map.of(1, 2, 5, 1, 6, 1), player.dice().getDiceSetMap());

		actionManager.removeDice(5);
		assertEquals(Map.of(1, 2, 6, 1), player.dice().getDiceSetMap());

		actionManager.removeAllDice();
		assertTrue(player.dice().getDiceSetMap().isEmpty());
		assertEquals(0, player.dice().getNumDiceInPlay());

		actionManager.replenishAllDice();
		assertEquals(6, player.dice().getNumDiceInPlay());
		assertTrue(player.dice().getDiceSetMap().isEmpty());
	}

	@Test
	void switchToNextPlayerAndFindHighestScoreDelegateToPlayerManager() {
		Player alice = TestDoubles.player("Alice");
		Player bob = TestDoubles.player("Bob");
		bob.score().increasePermanentScore(3200);
		StubPlayerManager playerManager = new StubPlayerManager(List.of(alice, bob));
		ActionManager actionManager = new ActionManager(playerManager, new SequencedDiceManager(), 5000);

		actionManager.switchToNextPlayer();

		assertEquals(1, playerManager.switchCalls);
		assertSame(bob, actionManager.getCurrentPlayer());
		assertSame(bob, actionManager.findHighestScoringPlayer());
	}

	@Test
	void gameEndingPlayerControlsGameOverState() {
		Player player = TestDoubles.player("Jacob");
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);

		assertFalse(actionManager.isGameOver());

		actionManager.setGameEndingPlayer(player);

		assertTrue(actionManager.isGameOver());
		assertSame(player, actionManager.getGameEndingPlayer());
	}
}
