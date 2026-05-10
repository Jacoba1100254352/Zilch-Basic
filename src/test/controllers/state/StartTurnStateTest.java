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
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


class StartTurnStateTest
{
	@Test
	void handleResetsTurnStateAndRestoresAllDice() {
		Player player = TestDoubles.playerWithDice("Jacob", Map.of(1, 2, 5, 1));
		player.score().setRoundScore(350);
		player.score().setScoreFromMultiples(300);
		SequencedDiceManager diceManager = new SequencedDiceManager();
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), diceManager, 5000);
		GameOptionManager gameOptionManager = new GameOptionManager(new RuleManager(new RuleRegistry()));
		GameOption selectedOption = new GameOption(RuleType.SINGLE, "Single", "desc", 1, 100, Map.of(1, 1));
		gameOptionManager.setSelectedGameOption(selectedOption);

		TurnContext turnContext = new TurnContext(player);
		turnContext.setSelectedOption(selectedOption);
		turnContext.getScoredMultiples().put(3, 3);

		GamePhase nextPhase = new StartTurnState(actionManager, gameOptionManager).handle(turnContext);

		assertEquals(GamePhase.ROLL_DICE, nextPhase);
		assertEquals(0, player.score().getRoundScore());
		assertEquals(0, player.score().getScoreFromMultiples());
		assertTrue(player.dice().getDiceSetMap().isEmpty());
		assertEquals(6, player.dice().getNumDiceInPlay());
		assertTrue(turnContext.getScoredMultiples().isEmpty());
		assertNull(turnContext.getSelectedOption());
		assertNull(gameOptionManager.getSelectedGameOption());
	}
}
