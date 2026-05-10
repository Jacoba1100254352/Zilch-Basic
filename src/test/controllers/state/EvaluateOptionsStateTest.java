package controllers.state;


import model.entities.Player;
import model.managers.GameOptionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import support.TestDoubles;
import support.TestDoubles.RecordingMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class EvaluateOptionsStateTest
{
	@Test
	void handleMarksBustWhenNoOptionsAreAvailable() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(2, 2, 3, 2, 4, 2));
		player.score().setRoundScore(250);
		player.score().setScoreFromMultiples(200);
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.SINGLE, Set.of(1, 5)));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RecordingMessage uiManager = new RecordingMessage();
		TurnContext turnContext = new TurnContext(player);

		GamePhase nextPhase = new EvaluateOptionsState(gameOptionManager, uiManager).handle(turnContext);

		assertEquals(GamePhase.END_TURN, nextPhase);
		assertTrue(turnContext.isBusted());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(0, player.score().getScoreFromMultiples());
		assertEquals(List.of("Bust! No scoring options are available.\n"), uiManager.waitingMessages);
	}

	@Test
	void handleMovesToSelectionWhenScoringOptionsExist() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(1, 1, 2, 5));
		RuleManager ruleManager = new RuleManager(new RuleRegistry());
		ruleManager.initializeRules(Map.of(RuleType.SINGLE, Set.of(1, 5)));
		GameOptionManager gameOptionManager = new GameOptionManager(ruleManager);
		RecordingMessage uiManager = new RecordingMessage();
		TurnContext turnContext = new TurnContext(player);

		GamePhase nextPhase = new EvaluateOptionsState(gameOptionManager, uiManager).handle(turnContext);

		assertEquals(GamePhase.SELECT_OPTION, nextPhase);
		assertFalse(turnContext.isBusted());
		assertEquals(1, gameOptionManager.getGameOptions().size());
		assertTrue(uiManager.waitingMessages.isEmpty());
	}
}
