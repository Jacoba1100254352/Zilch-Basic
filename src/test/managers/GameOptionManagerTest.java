package managers;


import modelManagers.PlayerManager;
import models.Dice;
import models.GameOption;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.ConsoleUserInputHandler;
import ui.UserInputHandler;

import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;
import static org.junit.jupiter.api.Assertions.*;


class GameOptionManagerTest
{
	
	private GameOptionManager gameOptionManager;
	private PlayerManager playerManager;
	
	@BeforeEach
	void setUp() {
		// 1) Create the real GameCoordinator (which includes real RuleManager, etc.).
		GameCoordinator gameCoordinator = new GameCoordinator();
		
		// 2) We don't actually need console input for these tests, so you
		//    could attach a "fake" user input handler or just leave it null
		//    as long as we don't actually call gameCoordinator.setupGame() or playGame().
		//    In any case, here's how you'd attach a real console handler if you wanted:
		UserInputHandler fakeInput = new ConsoleUserInputHandler(gameCoordinator);
		gameCoordinator.setUserInputHandler(fakeInput);
		
		// 3) Manually create a PlayerManager with one test player.
		//    Suppose we let them have a 5000 score limit so we don't accidentally “end” the game too soon.
		playerManager = new PlayerManager(List.of("TestPlayer"), 5000);
		gameCoordinator.setPlayerManager(playerManager);
		
		// 4) Extract the already-constructed GameOptionManager from GameCoordinator.
		gameOptionManager = gameCoordinator.getGameOptionManager();
	}
	
	@Test
	void testEvaluateGameOptions_Strait() {
		// Give the player a dice set corresponding to a perfect strait (1..6).
		Player p = playerManager.getCurrentPlayer();
		Dice dice = p.dice();
		
		// Clear whatever might be there, then set each face to 1
		dice.getDiceSetMap().clear();
		for (int face = 1; face <= FULL_SET_OF_DICE; face++) {
			dice.getDiceSetMap().put(face, 1);
		}
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect exactly one option: STRAIT
		List<GameOption> options = gameOptionManager.getGameOptions();
		assertEquals(3, options.size(), "Should only have 3 options: Strait, Single 1, and Single 5");
		GameOption opt = options.get(0);
		assertEquals(GameOption.Type.STRAIT, opt.type());
		assertNull(opt.value(), "STRAIT has no numeric value");
	}
	
	@Test
	void testEvaluateGameOptions_AddMultiple_1() {
		// Give the player a dice set corresponding to a set of 3 ones.
		Player p = playerManager.getCurrentPlayer();
		Dice dice = p.dice();
		
		int multiple = 1;
		int points;
		
		// Clear whatever might be there, then set to 1,1,1,4,6,6
		dice.getDiceSetMap().clear();
		dice.getDiceSetMap().put(multiple, 3);
		dice.getDiceSetMap().put(4, 1);
		dice.getDiceSetMap().put(6, 2);
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect exactly one option: STRAIT
		List<GameOption> options = gameOptionManager.getGameOptions();
		assertEquals(2, options.size(), "Should only have 2 options: Multiple (of 1s), Single 1");
		GameOption opt = options.getFirst();
		assertEquals(GameOption.Type.MULTIPLE, opt.type());
		assertEquals(multiple, opt.value(), "The multiple 1 should be available");
		
		dice.getDiceSetMap().put(multiple, 0);
		points = (multiple == 1) ? 1000 : multiple * 100;
		playerManager.getCurrentPlayer().score().setScoreFromMultiples(points);
		playerManager.getCurrentPlayer().score().setRoundScore(points);
		gameOptionManager.setPreviouslySelectedMultipleValue(multiple);
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect ...
		options = gameOptionManager.getGameOptions();
		assertEquals(0, options.size(), "Should only have no option");
		
		// There should now just be 1,4,6
		dice.getDiceSetMap().clear();
		dice.getDiceSetMap().put(multiple, 1);
		dice.getDiceSetMap().put(4, 1);
		dice.getDiceSetMap().put(6, 1);
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect ...
		options = gameOptionManager.getGameOptions();
		assertEquals(2, options.size(), "Should only have 2 options: Add Multiple (1), and Single 1");
		opt = options.getFirst();
		assertEquals(GameOption.Type.ADD_MULTIPLE, opt.type());
		assertEquals(multiple, opt.value(), "The multiple 1 should be available");
	}
	
	@Test
	void testEvaluateGameOptions_AddMultiple_3() {
		// Give the player a dice set corresponding to a set of 3 ones.
		Player p = playerManager.getCurrentPlayer();
		Dice dice = p.dice();
		
		int multiple = 3;
		int points;
		
		// Clear whatever might be there, then set to 1,1,1,4,6,6
		dice.getDiceSetMap().clear();
		dice.getDiceSetMap().put(multiple, 3);
		dice.getDiceSetMap().put(4, 1);
		dice.getDiceSetMap().put(6, 2);
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect ...
		List<GameOption> options = gameOptionManager.getGameOptions();
		assertEquals(1, options.size(), "Should only have 1 options: Multiple (of 3s)");
		GameOption opt = options.getFirst();
		assertEquals(GameOption.Type.MULTIPLE, opt.type());
		assertEquals(multiple, opt.value(), "The multiple 3 should be available");
		
		dice.getDiceSetMap().put(multiple, 0);
		points = (multiple == 1) ? 1000 : multiple * 100;
		playerManager.getCurrentPlayer().score().setScoreFromMultiples(points);
		playerManager.getCurrentPlayer().score().setRoundScore(points);
		gameOptionManager.setPreviouslySelectedMultipleValue(multiple);
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect ...
		options = gameOptionManager.getGameOptions();
		assertEquals(0, options.size(), "Should only have no option");
		
		// There should now just be 3,4,6
		dice.getDiceSetMap().clear();
		dice.getDiceSetMap().put(multiple, 1);
		dice.getDiceSetMap().put(4, 1);
		dice.getDiceSetMap().put(6, 1);
		dice.calculateNumDiceInPlay();
		
		// Evaluate the options
		gameOptionManager.evaluateGameOptions();
		
		// Expect exactly one option: STRAIT
		options = gameOptionManager.getGameOptions();
		assertEquals(1, options.size(), "Should only have 1 option: Add Multiple (3)");
		opt = options.getFirst();
		assertEquals(GameOption.Type.ADD_MULTIPLE, opt.type());
		assertEquals(multiple, opt.value(), "The add multiple 3 should be available");
	}
	
	@Test
	void testEvaluateGameOptions_Set() {
		// A "set" by your code is 3 distinct faces, each repeated 2 times, e.g. 1,1,2,2,3,3
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(1, 2);
		map.put(2, 2);
		map.put(3, 2);
		p.dice().calculateNumDiceInPlay();
		
		gameOptionManager.evaluateGameOptions();
		List<GameOption> options = gameOptionManager.getGameOptions();
		
		assertEquals(2, options.size(), "Valid options for a 1,1,2,2,3,3: Set, Single 1");
		assertEquals(GameOption.Type.SET, options.get(0).type());
	}
	
	@Test
	void testEvaluateGameOptions_Multiple() {
		// Put in 4 copies of '3', so there's a multiple(3)
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(3, 4);
		p.dice().calculateNumDiceInPlay();
		
		gameOptionManager.evaluateGameOptions();
		List<GameOption> options = gameOptionManager.getGameOptions();
		
		// We expect a MULTIPLE(3) among the options
		boolean hasMultiple3 = options.stream()
		                              .anyMatch(o -> o.type() == GameOption.Type.MULTIPLE && o.value() == 3);
		assertTrue(hasMultiple3, "Should have MULTIPLE(3) for dice = 3,3,3,3");
	}
	
	@Test
	void testEvaluateGameOptions_Single() {
		// The logic for single is that it appears if there's at least one '1' or '5'
		// so let's put in one '5'
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(5, 1);
		p.dice().calculateNumDiceInPlay();
		
		gameOptionManager.evaluateGameOptions();
		List<GameOption> options = gameOptionManager.getGameOptions();
		
		boolean hasSingle5 = options.stream()
		                            .anyMatch(o -> o.type() == GameOption.Type.SINGLE && o.value() == 5);
		assertTrue(hasSingle5, "Should have SINGLE(5) for one '5'");
	}
	
	@Test
	void testProcessMove_StraitScoresAndRemovesDice() {
		// Setup a strait
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		for (int i = 1; i <= 6; i++) {
			map.put(i, 1);
		}
		p.dice().calculateNumDiceInPlay();
		
		// Evaluate => Add STRAIT option
		gameOptionManager.evaluateGameOptions();
		// Confirm we have it
		assertFalse(gameOptionManager.getGameOptions().isEmpty());
		// Pick the strait
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.STRAIT, null));
		// Should be valid
		assertTrue(gameOptionManager.isValidMove());
		
		// Process
		gameOptionManager.processMove();
		
		// Confirm scoring: straits are worth 1000 in code (scoreStraits())
		// so the player's round score should now be 1000
		assertEquals(1000, p.score().getRoundScore(), "Strait should give 1000 round points");
		
		// Also confirm the dice were removed
		assertEquals(0, p.dice().getNumDiceInPlay(), "Should have removed all dice on a strait");
	}
	
	@Test
	void testProcessMove_Set() {
		// By your code, a set is also 1000 points, then remove all dice
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(1, 2);
		map.put(2, 2);
		map.put(3, 2);
		p.dice().calculateNumDiceInPlay();
		
		// Evaluate => set
		gameOptionManager.evaluateGameOptions();
		assertFalse(gameOptionManager.getGameOptions().isEmpty());
		// We'll pick the SET option
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.SET, null));
		assertTrue(gameOptionManager.isValidMove());
		
		gameOptionManager.processMove();
		assertEquals(1000, p.score().getRoundScore(), "A set is worth 1000 points");
		assertEquals(0, p.dice().getNumDiceInPlay(), "All dice removed after scoring a set");
	}
	
	@Test
	void testProcessMove_Multiple() {
		// Suppose dice is 3,3,3 => the code for multiple(3) yields 300
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(3, 3);
		p.dice().calculateNumDiceInPlay();
		
		// Evaluate => MULTIPLE(3)
		gameOptionManager.evaluateGameOptions();
		List<GameOption> opts = gameOptionManager.getGameOptions();
		boolean foundMultiple3 = opts.stream()
		                             .anyMatch(o -> o.type() == GameOption.Type.MULTIPLE && o.value() == 3);
		assertTrue(foundMultiple3, "Should have MULTIPLE(3) among the options");
		
		// Pick the MULTIPLE(3) option
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 3));
		assertTrue(gameOptionManager.isValidMove());
		
		// processMove => calls scoreMultiple(3)
		gameOptionManager.processMove();
		
		// By your ScoreManager logic:
		// For 3,3,3, baseScore = 3 * 100 = 300 (since the triple is not 1,1,1)
		// Round score should be 300
		assertEquals(300, p.score().getRoundScore(), "Triple-3 should yield 300 round points");
		
		// The dice used for the multiple are removed => 0 dice left
		assertEquals(0, p.dice().getNumDiceInPlay());
	}
	
	@Test
	void testProcessMove_Single() {
		// One '5' => Single(5) is worth 50 points, remove 1 die
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(5, 1);
		p.dice().calculateNumDiceInPlay();
		
		gameOptionManager.evaluateGameOptions();
		// Should have SINGLE(5)
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.SINGLE, 5));
		assertTrue(gameOptionManager.isValidMove());
		
		gameOptionManager.processMove();
		assertEquals(50, p.score().getRoundScore(), "Single 5 is worth 50 points");
		assertEquals(0, p.dice().getNumDiceInPlay(), "Removed that one 5 die");
	}
	
	@Test
	void testProcessMove_NoOptionSelected() {
		// If we never set a selected option, isValidMove() = false
		// and processMove() should say "No option selected."
		// We'll confirm it does not add to the player's round score.
		
		Player p = playerManager.getCurrentPlayer();
		p.score().setRoundScore(123); // just to see if it changes
		
		// No call to setSelectedGameOption
		gameOptionManager.processMove();
		
		// The code prints "No option selected." but let's just confirm it didn't
		// affect the player's round score or dice
		assertEquals(123, p.score().getRoundScore(), "Should remain unchanged");
	}
	
	@Test
	void testProcessMove_InvalidMove() {
		// e.g. we set a MULTIPLE(3) but we only have a single 3
		Player p = playerManager.getCurrentPlayer();
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(3, 1); // Not enough for a triple
		p.dice().calculateNumDiceInPlay();
		
		// Evaluate => we might not get MULTIPLE(3)
		gameOptionManager.evaluateGameOptions();
		// But let's forcibly pick MULTIPLE(3)
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 3));
		
		// That won't be in the list => isValidMove() = false
		assertFalse(gameOptionManager.isValidMove());
		
		// processMove => "Invalid move selected."
		gameOptionManager.processMove();
		
		// Check no score was added
		assertEquals(0, p.score().getRoundScore());
	}
	
	@Test
	void testAddMultipleRule() {
		// The logic for "add multiple" means that you first have a multiple, e.g. 1,1,1 => you pick MULTIPLE(1).
		// Then if you roll more 1's, you can pick ADD_MULTIPLE(1).
		//
		// We'll simulate that by:
		// 1) The user first chooses multiple(1) for dice 1,1,1 => 1000 points
		// 2) Then the dice is refilled with 2 more 1's => "1,1", so the AddMultipleRule(1) is valid if
		//    previouslySelectedMultipleValue is 1.
		// 3) If we process that move, we get the new total using the code from ScoreManager.
		
		Player p = playerManager.getCurrentPlayer();
		// Step 1: triple 1 => 1,1,1
		Map<Integer, Integer> map = p.dice().getDiceSetMap();
		map.clear();
		map.put(1, 3);
		p.dice().calculateNumDiceInPlay();
		
		// Evaluate => MULTIPLE(1)
		gameOptionManager.evaluateGameOptions();
		// pick MULTIPLE(1)
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 1));
		// process => should do 1000 points, eliminate dice
		gameOptionManager.processMove();
		// Now the player's round score is 1000
		assertEquals(1000, p.score().getRoundScore());
		// Also previouslySelectedMultipleValue is 1
		assertEquals(1, gameOptionManager.getPreviouslySelectedMultipleValue());
		
		// Suppose the next roll yields 1,1 => so we set that in the dice
		map.clear();
		map.put(1, 2);
		p.dice().calculateNumDiceInPlay();
		
		// Evaluate again => we expect ADD_MULTIPLE(1)
		gameOptionManager.evaluateGameOptions();
		List<GameOption> opts = gameOptionManager.getGameOptions();
		boolean foundAddMultiple = opts.stream().anyMatch(o ->
				                                                  o.type() == GameOption.Type.ADD_MULTIPLE && o.value() == 1
		);
		assertTrue(foundAddMultiple, "We should have an ADD_MULTIPLE(1) now that previouslySelectedMultipleValue is 1 and we rolled more 1s.");
		
		// Choose it
		gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.ADD_MULTIPLE, 1));
		assertTrue(gameOptionManager.isValidMove());
		
		// process => that triggers "scoreMultiple(1)" again, but with the "add multiple" logic.
		// We can confirm the final round score is bigger.
		gameOptionManager.processMove();
		
		// Checking the exact final score for "1,1" after an existing multiple(1)
		// is in your ScoreManager's "Add Multiple" logic.
		// That code does:  mScore = (2^(countOfDice)) * previousMultipleScore
		// Here, previousMultipleScore was 1000, countOfDice = 2 => 2^(2) = 4 => 4 * 1000 = 4000 total
		// So new round score = 4000 total.
		// The difference from the old 1000 is +3000, so final roundScore = 4000
		assertEquals(4000, p.score().getRoundScore(), "AddMultiple(1) with 2 more 1's should yield 4000 total");
	}
}
