package controllers.state;


import model.entities.GameOption;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import support.TestDoubles;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ApplyOptionStateTest
{
	@Test
	void handleAppliesTheSelectedOptionAndConsumesDice() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(1, 1, 2, 5));
		SequencedDiceManager diceManager = new SequencedDiceManager();
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.SINGLE, Set.of(1, 5)));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		GameOption option = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, Map.of(1, 1));
		RecordingMessage uiManager = new RecordingMessage();
		TurnContext turnContext = new TurnContext(player);
		turnContext.setSelectedOption(option);

		GamePhase nextPhase = new ApplyOptionState(actionManager, gameOptionManager, uiManager).handle(turnContext);

		assertEquals(GamePhase.DECIDE_TURN, nextPhase);
		assertEquals(100, player.score().getRoundScore());
		assertEquals(Map.of(2, 5), player.dice().getDiceSetMap());
		assertEquals(5, player.dice().getNumDiceInPlay());
		assertTrue(uiManager.waitingMessages.isEmpty());
	}

	@Test
	void handleReplenishesDiceAndShowsHotDiceMessageWhenAllDiceScore() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(1, 2, 2, 2, 5, 2));
		SequencedDiceManager diceManager = new SequencedDiceManager();
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		Map<RuleType, Object> selectedRules = new LinkedHashMap<>();
		selectedRules.put(RuleType.SET, 3);
		ruleManager.initializeRules(selectedRules);
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		GameOption option = new GameOption(RuleType.SET, "Set", "desc", null, 1000, Map.of(1, 2, 2, 2, 5, 2));
		RecordingMessage uiManager = new RecordingMessage();
		TurnContext turnContext = new TurnContext(player);
		turnContext.setSelectedOption(option);

		GamePhase nextPhase = new ApplyOptionState(actionManager, gameOptionManager, uiManager).handle(turnContext);

		assertEquals(GamePhase.DECIDE_TURN, nextPhase);
		assertEquals(1000, player.score().getRoundScore());
		assertTrue(player.dice().getDiceSetMap().isEmpty());
		assertEquals(6, player.dice().getNumDiceInPlay());
		assertEquals(List.of("Hot dice! All dice scored. Rolling all six dice again.\n"), uiManager.waitingMessages);
	}

	@Test
	void hotDiceEndsTheMultipleExtensionChainBeforeTheNextRoll() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(3, 6));
		SequencedDiceManager diceManager = new SequencedDiceManager();
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(
				RuleType.MULTIPLE, 3,
				RuleType.SINGLE, Set.of(1, 5)
		));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		TurnContext turnContext = new TurnContext(player);
		gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());
		turnContext.setSelectedOption(gameOptionManager.getGameOptions().stream()
				.filter(option -> option.type().equals(RuleType.MULTIPLE))
				.findFirst()
				.orElseThrow());

		new ApplyOptionState(actionManager, gameOptionManager, new RecordingMessage()).handle(turnContext);
		player.dice().setDiceSetMap(Map.of(3, 1, 1, 1, 2, 2, 4, 2));
		player.dice().calculateNumDiceInPlay();
		gameOptionManager.evaluateGameOptions(turnContext.toRuleContext());

		assertTrue(turnContext.getScoredMultiples().isEmpty());
		assertFalse(gameOptionManager.getGameOptions().stream()
				.anyMatch(option -> option.type().equals(RuleType.ADD_MULTIPLE)));
		assertTrue(gameOptionManager.getGameOptions().stream()
				.anyMatch(option -> option.type().equals(RuleType.SINGLE) && option.selectedValue() == 1));
	}
}
