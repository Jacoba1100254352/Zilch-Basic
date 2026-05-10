package controllers;


import eventHandling.events.Event;
import eventHandling.events.EventDataKey;
import eventHandling.events.GameEventType;
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


class GameServerTest
{
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
	void playGameRunsFinalRoundFlowAndAnnouncesWinner() throws Exception {
		RecordingEventDispatcher dispatcher = new RecordingEventDispatcher();
		Player alice = TestDoubles.player("Alice");
		Player bob = TestDoubles.player("Bob");
		StubPlayerManager playerManager = new StubPlayerManager(List.of(alice, bob));
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
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

	private IRuleManager configuredRuleManager() {
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		Map<RuleType, Object> selectedRules = new LinkedHashMap<>();
		selectedRules.put(RuleType.SINGLE, Set.of(1, 5));
		ruleManager.initializeRules(selectedRules);
		return ruleManager;
	}
}
