package ui;


import model.entities.GameOption;
import model.entities.Player;
import model.entities.TurnContinuation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import rules.managers.RuleRegistry;
import rules.managers.RuleType;
import rules.variable.IRule;
import support.TestDoubles;
import support.TestDoubles.QueueInputManager;
import support.TestDoubles.RecordingMessage;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class UserInteractionManagerTest
{
	private RecordingMessage gameplayUI;
	private QueueInputManager inputManager;
	private UserInteractionManager userInteractionManager;

	@BeforeEach
	void setUp() {
		gameplayUI = new RecordingMessage();
		inputManager = new QueueInputManager();
		userInteractionManager = new UserInteractionManager(gameplayUI, inputManager);
	}

	@Test
	void selectRulesTreatsSingleLetterYesAsEnabled() {
		List<IRule> selectableRules = selectableRules();
		inputManager.addString("y");
		for (int index = 1; index < selectableRules.size(); index++) {
			inputManager.addString("n");
		}

		Map<RuleType, Object> selectedRules = userInteractionManager.selectRules();

		assertEquals(1, selectedRules.size());
		assertTrue(selectedRules.containsKey(selectableRules.get(0).getRuleType()));
	}

	@Test
	void selectRulesRetriesUntilAtLeastOneRuleIsEnabled() {
		List<IRule> selectableRules = selectableRules();
		for (int index = 0; index < selectableRules.size(); index++) {
			inputManager.addString("n");
		}
		inputManager.addString("y");
		for (int index = 1; index < selectableRules.size(); index++) {
			inputManager.addString("n");
		}

		Map<RuleType, Object> selectedRules = userInteractionManager.selectRules();

		assertEquals(1, selectedRules.size());
		assertTrue(gameplayUI.messageCalls.contains("At least one scoring rule must be enabled. Please choose again.\n"));
	}

	@Test
	void selectRulesDoesNotAllowOnlyNonScoringGameVariants() {
		List<IRule> selectableRules = selectableRules();
		queueRuleSelections(selectableRules, Set.of(RuleType.FIRST_ROLL_BUST));
		queueRuleSelections(selectableRules, Set.of(RuleType.SINGLE));

		Map<RuleType, Object> selectedRules = userInteractionManager.selectRules();

		assertEquals(1, selectedRules.size());
		assertTrue(selectedRules.containsKey(RuleType.SINGLE));
		assertTrue(gameplayUI.messageCalls.contains("At least one scoring rule must be enabled. Please choose again.\n"));
	}

	@Test
	void chooseGameOptionRepromptsAfterInvalidSelection() {
		Player player = TestDoubles.player("Jacob");
		List<GameOption> options = List.of(
				new GameOption(RuleType.SINGLE, "Single", "Single one", 1, 100, Map.of(1, 1)),
				new GameOption(RuleType.SET, "Set", "Three pairs", null, 1000, Map.of(1, 2, 2, 2, 5, 2))
		);
		inputManager.addInt(0).addInt(2);

		GameOption selectedOption = userInteractionManager.chooseGameOption(player, options);

		assertEquals(options.get(1), selectedOption);
		assertTrue(gameplayUI.messageCalls.contains("Invalid choice. Please select a valid option: "));
	}

	@Test
	void getNumberOfPlayersRepromptsUntilTheValueIsWithinRange() {
		inputManager.addInt(0).addInt(7).addInt(3);

		int numPlayers = userInteractionManager.getNumberOfPlayers();

		assertEquals(3, numPlayers);
		assertEquals(
				List.of(
						"Enter the number of players (1-6): ",
						"Invalid number. Please enter a number between 1 and 6: ",
						"Invalid number. Please enter a number between 1 and 6: "
				),
				gameplayUI.messageCalls
		);
	}

	@Test
	void getPlayerNamesCollectsEachPromptedName() {
		inputManager.addString("Alice").addString("Bob").addString("Charlie");

		List<String> playerNames = userInteractionManager.getPlayerNames(3);

		assertEquals(List.of("Alice", "Bob", "Charlie"), playerNames);
	}

	@Test
	void getValidScoreLimitRejectsValuesBelowTheMinimum() {
		inputManager.addInt(500).addInt(1500);

		int scoreLimit = userInteractionManager.getValidScoreLimit();

		assertEquals(1500, scoreLimit);
		assertTrue(gameplayUI.messageCalls.contains("Invalid score limit. Score limit must be at least 1000. Please try again."));
	}

	@Test
	void shouldRollAgainReturnsTrueImmediatelyWhenThePlayerCannotBank() {
		Player player = TestDoubles.player("Jacob");

		boolean shouldRollAgain = userInteractionManager.shouldRollAgain(player, false, 1000);

		assertTrue(shouldRollAgain);
		assertTrue(gameplayUI.messageCalls.contains("Jacob cannot bank points yet. You need 1000 points to open.\n"));
	}

	@Test
	void shouldRollAgainUsesYesNoInputWhenThePlayerCanBank() {
		Player player = TestDoubles.player("Jacob");
		inputManager.addString("maybe").addString("n");

		boolean shouldRollAgain = userInteractionManager.shouldRollAgain(player, true, 1000);

		assertFalse(shouldRollAgain);
		assertTrue(gameplayUI.messageCalls.contains("Invalid input. Please enter 'yes' or 'no' [y/n]: "));
	}

	@Test
	void getValidOpeningScoreLimitRejectsValuesOutsideTheConfiguredGameRange() {
		inputManager.addInt(-50).addInt(2500).addInt(750);

		int openingScoreLimit = userInteractionManager.getValidOpeningScoreLimit(2000);

		assertEquals(750, openingScoreLimit);
		assertEquals(
				2,
				gameplayUI.messageCalls.stream()
				             .filter(message -> message.startsWith("Invalid opening score."))
				             .count()
		);
	}

	@Test
	void shouldStealExplainsTheInheritedScoreAndRemainingDice() {
		Player player = TestDoubles.player("Bob");
		TurnContinuation continuation = new TurnContinuation("Alice", 450, 3, Map.of(2, 3));
		inputManager.addString("y");

		assertTrue(userInteractionManager.shouldSteal(player, continuation));
		assertTrue(
				gameplayUI.messageCalls.stream().anyMatch(message ->
						message.contains("steal 450 points from Alice") && message.contains("3 remaining dice")
				)
		);
	}

	private List<IRule> selectableRules() {
		return new RuleRegistry().getAvailableRules()
		                         .stream()
		                         .filter(IRule::isSelectableAtSetup)
		                         .toList();
	}

	private void queueRuleSelections(List<IRule> selectableRules, Set<RuleType> enabledRules) {
		for (IRule rule : selectableRules) {
			inputManager.addString(enabledRules.contains(rule.getRuleType()) ? "y" : "n");
		}
	}
}
