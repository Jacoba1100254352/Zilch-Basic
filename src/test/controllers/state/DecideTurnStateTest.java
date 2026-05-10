package controllers.state;


import model.entities.Player;
import model.managers.ActionManager;
import org.junit.jupiter.api.Test;
import support.TestDoubles;
import support.TestDoubles.ScriptedUserInteraction;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class DecideTurnStateTest
{
	@Test
	void handleReturnsRollDiceWhenThePlayerKeepsRolling() {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(1000);
		player.score().setRoundScore(250);
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction().addRollAgainDecision(true);

		GamePhase nextPhase = new DecideTurnState(actionManager, userInteraction).handle(new TurnContext(player));

		assertEquals(GamePhase.ROLL_DICE, nextPhase);
		assertEquals(1000, player.score().getPermanentScore());
		assertEquals(250, player.score().getRoundScore());
	}

	@Test
	void handleBanksPointsAndEndsTurnWhenThePlayerStops() {
		Player player = TestDoubles.player("Jacob");
		player.score().increasePermanentScore(1000);
		player.score().setRoundScore(300);
		player.score().setScoreFromMultiples(250);
		ActionManager actionManager = new ActionManager(new StubPlayerManager(List.of(player)), new SequencedDiceManager(), 5000);
		ScriptedUserInteraction userInteraction = new ScriptedUserInteraction().addRollAgainDecision(false);

		GamePhase nextPhase = new DecideTurnState(actionManager, userInteraction).handle(new TurnContext(player));

		assertEquals(GamePhase.END_TURN, nextPhase);
		assertEquals(1300, player.score().getPermanentScore());
		assertEquals(0, player.score().getRoundScore());
		assertEquals(0, player.score().getScoreFromMultiples());
	}
}
