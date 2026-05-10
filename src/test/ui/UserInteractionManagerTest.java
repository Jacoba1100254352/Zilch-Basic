package ui;


import model.entities.GameOption;
import model.entities.Player;
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

		boolean shouldRollAgain = userInteractionManager.shouldRollAgain(player, false);

		assertTrue(shouldRollAgain);
		assertTrue(gameplayUI.messageCalls.contains("Jacob cannot bank points yet. You need 1000 points to open.\n"));
	}

	@Test
	void shouldRollAgainUsesYesNoInputWhenThePlayerCanBank() {
		Player player = TestDoubles.player("Jacob");
		inputManager.addString("maybe").addString("n");

		boolean shouldRollAgain = userInteractionManager.shouldRollAgain(player, true);

		assertFalse(shouldRollAgain);
		assertTrue(gameplayUI.messageCalls.contains("Invalid input. Please enter 'yes' or 'no' [y/n]: "));
	}

	private List<IRule> selectableRules() {
		return new RuleRegistry().getAvailableRules()
		                         .stream()
		                         .filter(IRule::isSelectableAtSetup)
		                         .toList();
	}
}
