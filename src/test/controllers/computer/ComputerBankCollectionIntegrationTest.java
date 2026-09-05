package controllers.computer;

import controllers.state.ApplyOptionState;
import controllers.state.DecideTurnState;
import controllers.state.GamePhase;
import controllers.state.StartTurnState;
import controllers.state.TurnContext;
import model.entities.ComputerDifficulty;
import model.entities.GameOption;
import model.entities.Player;
import model.managers.ActionManager;
import model.managers.GameOptionManager;
import org.junit.jupiter.api.Test;
import rules.managers.RuleManager;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import support.TestDoubles;
import ui.PlayerAwareUserInteraction;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ComputerBankCollectionIntegrationTest
{
	@Test
	void consoleCollectsAllThreeSinglesBeforeBanking2950() {
		Fixture fixture = fixture();
		fixture.player.score().setRoundScore(2700);
		fixture.player.dice().setDiceSetMap(new LinkedHashMap<>(Map.of(1, 2, 5, 1, 2, 1, 3, 1, 4, 1)));
		fixture.player.dice().calculateNumDiceInPlay();
		TurnContext turn = new TurnContext(fixture.player);
		GamePhase phase;
		int selections = 0;
		do {
			fixture.options.evaluateGameOptions(turn.toRuleContext());
			turn.setSelectedOption(fixture.interaction.chooseGameOption(fixture.player, fixture.options.getGameOptions()));
			phase = new ApplyOptionState(fixture.actions, fixture.options, new TestDoubles.RecordingMessage(), fixture.interaction)
					.handle(turn);
			assertTrue(++selections <= 3, "Selection must finish without rerolling or looping.");
		} while (phase == GamePhase.SELECT_OPTION);

		assertEquals(3, selections);
		assertEquals(GamePhase.DECIDE_TURN, phase);
		assertEquals(2950, fixture.player.score().getRoundScore());
		assertEquals(Map.of(2, 1, 3, 1, 4, 1), fixture.player.dice().getDiceSetMap());
		assertEquals(GamePhase.END_TURN, new DecideTurnState(fixture.actions, fixture.interaction).handle(turn));
		assertEquals(2950, fixture.player.score().getPermanentScore());
		assertEquals(0, fixture.dice.rollCalls);
	}

	@Test
	void consoleStartTurnClearsAnInterruptedBankCommitmentThroughTheInteraction() {
		Fixture fixture = fixture();
		fixture.player.score().setRoundScore(2800);
		fixture.player.dice().setNumDiceInPlay(5);
		GameOption one = new GameOption(RuleType.SINGLE, "Single", "One", 1, 100, Map.of(1, 1));
		assertTrue(fixture.interaction.shouldScoreMore(fixture.player, List.of(one)));

		new StartTurnState(fixture.actions, fixture.options, fixture.interaction).handle(new TurnContext(fixture.player));
		fixture.player.score().setRoundScore(2800);
		assertTrue(fixture.interaction.shouldRollAgain(fixture.player, true, 1000));
	}

	@Test
	void collectingTheLastDiceDoesNotUndoTheConsoleBankCommitment() {
		Fixture fixture = fixture();
		fixture.player.score().setRoundScore(2800);
		fixture.player.dice().setDiceSetMap(new LinkedHashMap<>(Map.of(1, 5)));
		fixture.player.dice().calculateNumDiceInPlay();
		// A singles-only roll isolates the collection path that eventually restores six dice.
		fixture.rules.initializeRules(Map.of(RuleType.SINGLE, Set.of(1, 5)));
		TurnContext turn = new TurnContext(fixture.player);
		fixture.options.evaluateGameOptions(turn.toRuleContext());
		assertTrue(fixture.interaction.shouldScoreMore(fixture.player, fixture.options.getGameOptions()));

		GamePhase phase;
		int selections = 0;
		do {
			fixture.options.evaluateGameOptions(turn.toRuleContext());
			turn.setSelectedOption(fixture.interaction.chooseGameOption(fixture.player, fixture.options.getGameOptions()));
			phase = new ApplyOptionState(fixture.actions, fixture.options, new TestDoubles.RecordingMessage(), fixture.interaction)
					.handle(turn);
			assertTrue(++selections <= 5);
		} while (phase == GamePhase.SELECT_OPTION);

		assertEquals(5, selections);
		assertEquals(3300, fixture.player.score().getRoundScore());
		assertEquals(6, fixture.player.dice().getNumDiceInPlay());
		assertFalse(fixture.strategy.shouldRollAgain(fixture.player, true));
	}

	private Fixture fixture() {
		Player player = TestDoubles.computerPlayer("Computer", ComputerDifficulty.HARD);
		TestDoubles.SequencedDiceManager dice = new TestDoubles.SequencedDiceManager();
		ActionManager actions = new ActionManager(
				new TestDoubles.StubPlayerManager(List.of(player, TestDoubles.player("Opponent"))), dice, 5000, 1000);
		ComputerStrategy strategy = new ComputerStrategy(actions, true, true, false);
		PlayerAwareUserInteraction interaction = new PlayerAwareUserInteraction(new TestDoubles.ScriptedUserInteraction(), strategy);
		RuleManager rules = new RuleManager(new RuleRegistry());
		rules.initializeRules(Map.of(
				RuleType.SINGLE, Set.of(1, 5), RuleType.MULTIPLE, 3, RuleType.SET, 3, RuleType.STRAIT, 6));
		return new Fixture(player, dice, actions, strategy, interaction, rules, new GameOptionManager(rules));
	}

	private record Fixture(
			Player player,
			TestDoubles.SequencedDiceManager dice,
			ActionManager actions,
			ComputerStrategy strategy,
			PlayerAwareUserInteraction interaction,
			RuleManager rules,
			GameOptionManager options
	) {
	}
}
