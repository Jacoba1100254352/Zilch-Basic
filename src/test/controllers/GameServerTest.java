package controllers;


import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
import model.entities.ComputerDifficulty;
import model.entities.Player;
import model.managers.ActionManager;
import org.junit.jupiter.api.Test;
import rules.managers.IRuleManager;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import support.TestDoubles;
import support.TestDoubles.RecordingEventDispatcher;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameServerTest
{
	@Test
	void gameServerRejectsAProfileWithoutScoringRules() {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player player = TestDoubles.player("Alice");
		ActionManager actionManager = new ActionManager(
				new StubPlayerManager(List.of(player)),
				new SequencedDiceManager(),
				5000
		);
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.FINAL_CHASE, true));

		assertThrows(
				IllegalArgumentException.class,
				() -> new GameServer(
						dispatcher,
						actionManager,
						ruleManager,
						new RecordingMessage(),
						5000,
						new ScriptedUserInteraction(),
						"game-id"
				)
		);
	}

	@Test
	void constructorRegistersGameOverListenerForScoreAndEndEvents() {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player player = TestDoubles.player("Alice");
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);
		IRuleManager ruleManager = configuredRuleManager();
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction();

		new GameServer(dispatcher, actionManager, ruleManager, uiManager, 5000, userInteraction, "game-id");

		assertEquals(1, dispatcher.listeners.get(GameEventType.GAME_OVER).size());
		assertEquals(1, dispatcher.listeners.get(GameEventType.SCORE_UPDATED).size());
	}

	@Test
	void computerOnlyConsoleGameCompletesWithoutRequestingHumanDecisions() throws Exception {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player computer = TestDoubles.computerPlayer("Computer", ComputerDifficulty.EASY);
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 4, 5, 1));
		ActionManager actionManager = new ActionManager(
				new StubPlayerManager(List.of(computer)),
				diceManager,
				100,
				0
		);
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction humanInteraction = new ScriptedUserInteraction();
		GameServer gameServer = new GameServer(
				dispatcher,
				actionManager,
				configuredRuleManager(false, true),
				uiManager,
				100,
				humanInteraction,
				"game-id"
		);

		gameServer.playGame();

		assertEquals(0, humanInteraction.chooseCalls);
		assertEquals(0, humanInteraction.scoreMoreCalls);
		assertEquals(0, humanInteraction.rollAgainCalls);
		assertEquals(List.of(computer), uiManager.winningPlayers);
		assertEquals(List.of(150), uiManager.winningScores);
		assertTrue(uiManager.messageCalls.contains("Computer scores another option from this roll.\n"));
		assertTrue(uiManager.messageCalls.contains("Computer banks 150 points.\n"));
	}

	@Test
	void playGameRunsFinalRoundFlowAndAnnouncesWinner() throws Exception {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player alice = TestDoubles.player("Alice");
		Player bob = TestDoubles.player("Bob");
		StubPlayerManager playerManager = new StubPlayerManager(List.of(alice, bob));
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(Map.of(2, 6))
				.queueRoll(Map.of(2, 6));
		ActionManager actionManager = new ActionManager(playerManager, diceManager, 100);
		IRuleManager ruleManager = configuredRuleManager();
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction().addRollAgainDecision(false);

		GameServer gameServer = new GameServer(dispatcher, actionManager, ruleManager, uiManager, 100, userInteraction, "game-id");
		gameServer.playGame();

		assertEquals(1, uiManager.welcomeCalls);
		assertEquals(List.of(alice), uiManager.lastRoundPlayers);
		assertEquals(List.of(alice), uiManager.winningPlayers);
		assertEquals(List.of(100), uiManager.winningScores);
		assertEquals(
				List.of(GameEventType.SCORE_UPDATED, GameEventType.SCORE_UPDATED, GameEventType.GAME_OVER),
				dispatcher.dispatchedEvents.stream().map(Event::getType).toList()
		);
	}

	@Test
	void handleLastTurnsGivesEachRemainingPlayerOneTurn() throws Exception {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		List<String> updatedPlayers = new ArrayList<>();
		dispatcher.addListener(GameEventType.SCORE_UPDATED, event -> {
			Player player = (Player) event.getData(EventDataKey.PLAYER);
			updatedPlayers.add(player.name());
		});

		StubPlayerManager playerManager = new StubPlayerManager(List.of(
				TestDoubles.player("Alice"),
				TestDoubles.player("Bob"),
				TestDoubles.player("Charlie")
		));
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1, 6, 1))
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1, 6, 1))
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1, 6, 1))
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1, 6, 1));
		ActionManager actionManager = new ActionManager(playerManager, diceManager, 5000);
		IRuleManager ruleManager = configuredRuleManager();
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction();
		GameServer gameServer = new GameServer(dispatcher, actionManager, ruleManager, uiManager, 5000, userInteraction, "game-id");

		actionManager.setGameEndingPlayer(playerManager.getCurrentPlayer());
		gameServer.handleLastTurns();

		assertEquals(List.of("Bob", "Charlie"), updatedPlayers);
	}

	@Test
	void disablingFinalChaseEndsTheGameWithoutExtraTurns() throws Exception {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player alice = TestDoubles.player("Alice");
		Player bob = TestDoubles.player("Bob");
		SequencedDiceManager diceManager = new SequencedDiceManager().queueRoll(Map.of(1, 1, 2, 5));
		ActionManager actionManager = new ActionManager(
				new StubPlayerManager(List.of(alice, bob)),
				diceManager,
				100,
				0
		);
		RecordingMessage uiManager = new RecordingMessage();
		GameServer gameServer = new GameServer(
				dispatcher,
				actionManager,
				configuredRuleManager(false, true),
				uiManager,
				100,
				new ScriptedUserInteraction().addRollAgainDecision(false),
				"game-id"
		);

		gameServer.playGame();

		assertTrue(uiManager.lastRoundPlayers.isEmpty());
		assertEquals(List.of(alice), uiManager.winningPlayers);
		assertEquals(1, diceManager.rollCalls);
		assertEquals(
				List.of(GameEventType.SCORE_UPDATED, GameEventType.GAME_OVER),
				dispatcher.dispatchedEvents.stream().map(Event::getType).toList()
		);
	}

	@Test
	void allowingTiesAnnouncesEveryPlayerAtTheFinalHighScore() throws Exception {
		RecordingMessage uiManager = playTwoPlayerTie(true);

		assertEquals(1, uiManager.tiePlayers.size());
		assertEquals(List.of("Alice", "Bob"), uiManager.tiePlayers.get(0).stream().map(Player::name).toList());
		assertEquals(List.of(100), uiManager.tieScores);
		assertTrue(uiManager.winningPlayers.isEmpty());
	}

	@Test
	void disablingTiesKeepsTheFirstPlayerToAttainTheFinalHighScore() throws Exception {
		RecordingMessage uiManager = playTwoPlayerTie(false);

		assertEquals(List.of("Alice"), uiManager.winningPlayers.stream().map(Player::name).toList());
		assertTrue(uiManager.tiePlayers.isEmpty());
	}

	private RecordingMessage playTwoPlayerTie(boolean allowTies) throws Exception {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player alice = TestDoubles.player("Alice");
		Player bob = TestDoubles.player("Bob");
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(Map.of(1, 1, 2, 5));
		ActionManager actionManager = new ActionManager(
				new StubPlayerManager(List.of(alice, bob)),
				diceManager,
				100,
				0
		);
		RecordingMessage uiManager = new RecordingMessage();
		GameServer gameServer = new GameServer(
				dispatcher,
				actionManager,
				configuredRuleManager(true, allowTies),
				uiManager,
				100,
				new ScriptedUserInteraction()
						.addRollAgainDecision(false)
						.addRollAgainDecision(false),
				"game-id"
		);

		gameServer.playGame();
		return uiManager;
	}

	private IRuleManager configuredRuleManager() {
		return configuredRuleManager(true, true);
	}

	private IRuleManager configuredRuleManager(boolean finalChase, boolean allowTies) {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		Map<RuleType, Object> selectedRules = new LinkedHashMap<>();
		selectedRules.put(RuleType.SINGLE, Set.of(1, 5));
		if (finalChase) {
			selectedRules.put(RuleType.FINAL_CHASE, true);
		}
		if (allowTies) {
			selectedRules.put(RuleType.ALLOW_TIES, true);
		}
		ruleManager.initializeRules(selectedRules);
		return ruleManager;
	}
}
