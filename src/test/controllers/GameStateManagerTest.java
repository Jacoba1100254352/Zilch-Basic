package controllers;


import controllers.state.GamePhase;
import controllers.state.TurnContext;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import support.TestDoubles;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class GameStateManagerTest
{
	@Test
	void processTurnMarksBustWhenNoOptionsExistAfterScoring() throws Exception {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(1000);
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(Map.of(2, 2, 3, 2, 4, 1));
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);

		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(
				RuleType.SINGLE, Set.of(1, 5),
				RuleType.FIRST_ROLL_BUST, 50
		));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.addRollAgainDecision(true);

		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager, userInteraction);
		TurnContext turnContext = new TurnContext(player);

		gameStateManager.processTurn(turnContext);

		assertTrue(turnContext.isBusted());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(GamePhase.END_TURN, gameStateManager.getCurrentPhase());
		assertEquals(List.of("Bust! No scoring options are available.\n"), uiManager.waitingMessages);
		assertEquals(1, userInteraction.chooseCalls);
		assertEquals(1, userInteraction.rollAgainCalls);
		assertEquals(2, diceManager.rollCalls);
	}

	@Test
	void processTurnAwardsFirstRollBustBonusAndContinues() throws Exception {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(1000);
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(2, 2, 3, 2, 4, 2))
				.queueRoll(Map.of(1, 1, 2, 5));
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);

		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(
				RuleType.SINGLE, Set.of(1, 5),
				RuleType.FIRST_ROLL_BUST, 50
		));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.addRollAgainDecision(false);

		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager, userInteraction);
		TurnContext turnContext = new TurnContext(player);

		gameStateManager.processTurn(turnContext);

		assertFalse(turnContext.isBusted());
		assertEquals(1150, player.score().getPermanentScore());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(GamePhase.END_TURN, gameStateManager.getCurrentPhase());
		assertEquals(List.of("First-roll bust! Awarded 50 points. Roll again.\n"), uiManager.waitingMessages);
		assertEquals(List.of("Jacob:0", "Jacob:50"), uiManager.currentScoreCalls);
		assertEquals(1, userInteraction.chooseCalls);
		assertEquals(1, userInteraction.rollAgainCalls);
		assertEquals(2, diceManager.rollCalls);
	}

	@Test
	void processTurnAppliesSelectedOptionAndBanksWhenPlayerStops() throws Exception {
		Player player = TestDoubles.player("Jacob");
		SequencedDiceManager diceManager = new SequencedDiceManager().queueRoll(Map.of(1, 2, 2, 2, 5, 2));
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);

		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		Map<RuleType, Object> selectedRules = new LinkedHashMap<>();
		selectedRules.put(RuleType.SINGLE, Set.of(1, 5));
		selectedRules.put(RuleType.SET, 3);
		ruleManager.initializeRules(selectedRules);
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.chooseWith(options -> options.stream()
				                              .filter(option -> option.type().equals(RuleType.SET))
				                              .findFirst()
				                              .orElseThrow())
				.addRollAgainDecision(false);

		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager, userInteraction);
		TurnContext turnContext = new TurnContext(player);

		gameStateManager.processTurn(turnContext);

		assertEquals(1000, player.score().getPermanentScore());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(6, player.dice().getNumDiceInPlay());
		assertTrue(turnContext.getScoredMultiples().isEmpty());
		assertNull(turnContext.getSelectedOption());
		assertEquals(GamePhase.END_TURN, gameStateManager.getCurrentPhase());
		assertEquals(List.of("Hot dice! All dice scored. Rolling all six dice again.\n"), uiManager.waitingMessages);
		assertEquals(List.of(true), userInteraction.canBankRequests);
		assertEquals(1, userInteraction.chooseCalls);
	}

	@Test
	void processTurnLoopsBackToRollDiceWhenPlayerChoosesToContinue() throws Exception {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(1000);
		SequencedDiceManager diceManager = new SequencedDiceManager()
				.queueRoll(Map.of(1, 1, 2, 5))
				.queueRoll(Map.of(5, 1, 2, 5));
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);

		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.SINGLE, Set.of(1, 5)));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RecordingMessage uiManager = new RecordingMessage();
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction()
				.addRollAgainDecision(true)
				.addRollAgainDecision(false);

		GameStateManager gameStateManager = new GameStateManager(gameOptionManager, uiManager, actionManager, userInteraction);
		TurnContext turnContext = new TurnContext(player);

		gameStateManager.processTurn(turnContext);

		assertEquals(2, diceManager.rollCalls);
		assertEquals(List.of("Jacob:0", "Jacob:100"), uiManager.currentScoreCalls);
		assertEquals(1150, player.score().getPermanentScore());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(List.of(true, true), userInteraction.canBankRequests);
		assertEquals(GamePhase.END_TURN, gameStateManager.getCurrentPhase());
	}
}
