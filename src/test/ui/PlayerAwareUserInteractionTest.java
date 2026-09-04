package ui;


import controllers.computer.ComputerStrategy;
import model.entities.ComputerDifficulty;
import model.entities.GameOption;
import model.entities.Player;
import model.entities.TurnContinuation;
import model.managers.ActionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleType;
import support.TestDoubles.RecordingMessage;
import support.TestDoubles.ScriptedUserInteraction;
import support.TestDoubles.SequencedDiceManager;
import support.TestDoubles.StubPlayerManager;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static support.TestDoubles.computerPlayer;
import static support.TestDoubles.player;


class PlayerAwareUserInteractionTest
{
	@Test
	void computerDecisionsBypassHumanPromptsWhileHumanDecisionsStillDelegate() {
		Player human = player("Alice");
		Player computer = computerPlayer("Computer", ComputerDifficulty.EASY);
		computer.dice().setNumDiceInPlay(3);
		computer.score().setRoundScore(550);
		ActionManager actionManager = new ActionManager(
				new StubPlayerManager(List.of(human, computer)),
				new SequencedDiceManager(),
				5000,
				0
		);
		ComputerStrategy strategy = new ComputerStrategy(actionManager, true, true, true);
		ScriptedUserInteraction humanInteraction = new ScriptedUserInteraction()
				.chooseWith(options -> options.get(options.size() - 1))
				.addRollAgainDecision(true)
				.addStealingDecision(false);
		RecordingMessage messages = new RecordingMessage();
		PlayerAwareUserInteraction interaction = new PlayerAwareUserInteraction(
				humanInteraction,
				strategy,
				messages
		);
		GameOption lowScore = option(50);
		GameOption highScore = option(100);
		List<GameOption> options = List.of(lowScore, highScore);

		assertEquals(highScore, interaction.chooseGameOption(computer, options));
		assertTrue(interaction.shouldScoreMore(computer, options));
		assertTrue(interaction.shouldRollAgain(computer, true, 0));
		computer.score().setRoundScore(600);
		assertFalse(interaction.shouldRollAgain(computer, true, 0));
		assertTrue(interaction.shouldSteal(computer, new TurnContinuation("You", 600, 3, Map.of())));
		assertEquals(0, humanInteraction.chooseCalls);
		assertEquals(0, humanInteraction.rollAgainCalls);
		assertEquals(0, humanInteraction.stealingCalls);
		assertTrue(messages.messageCalls.stream().anyMatch(message -> message.contains("selects Single [1]")));
		assertTrue(messages.messageCalls.contains("Computer scores another option from this roll.\n"));
		assertTrue(messages.messageCalls.contains("Computer rolls again.\n"));
		assertTrue(messages.messageCalls.contains("Computer banks 600 points.\n"));
		assertTrue(messages.messageCalls.stream().anyMatch(message -> message.contains("accepts your 600-point")));
		assertFalse(messages.messageCalls.stream().anyMatch(message -> message.contains("You's")));

		assertEquals(highScore, interaction.chooseGameOption(human, options));
		assertTrue(interaction.shouldRollAgain(human, true, 0));
		assertFalse(interaction.shouldSteal(human, new TurnContinuation("Computer", 600, 3, Map.of())));
		assertEquals(1, humanInteraction.chooseCalls);
		assertEquals(1, humanInteraction.rollAgainCalls);
		assertEquals(1, humanInteraction.stealingCalls);
	}

	private GameOption option(int points) {
		return new GameOption(
				RuleType.SINGLE,
				"Single",
				"Test option",
				points == 100 ? 1 : 5,
				points,
				Map.of(points == 100 ? 1 : 5, 1)
		);
	}
}
