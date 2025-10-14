import managers.GameCoordinator;
import managers.GameOptionManager;
import modelManagers.PlayerManager;
import models.Dice;
import models.GameOption;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ui.UserInputHandler;

import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;
import static org.junit.jupiter.api.Assertions.*;

class GameOptionManagerTest
{
        private GameCoordinator gameCoordinator;
        private GameOptionManager gameOptionManager;
        private PlayerManager playerManager;

        @BeforeEach
        void setUp() {
                gameCoordinator = new GameCoordinator();
                UserInputHandler fakeInput = new FakeUserInputHandler(gameCoordinator);
                gameCoordinator.setUserInputHandler(fakeInput);

                playerManager = new PlayerManager(List.of("TestPlayer"), 5000);
                gameCoordinator.setPlayerManager(playerManager);

                gameOptionManager = gameCoordinator.getGameOptionManager();
        }

        private Dice dice() {
                return playerManager.getCurrentPlayer().dice();
        }

        private void setDice(Map<Integer, Integer> counts) {
                Dice dice = dice();
                dice.getDiceSetMap().clear();
                counts.forEach((face, count) -> dice.getDiceSetMap().put(face, count));
                dice.calculateNumDiceInPlay();
        }

        private List<GameOption> evaluateOptions() {
                gameOptionManager.evaluateGameOptions();
                return gameOptionManager.getGameOptions();
        }

        @Test
        void testEvaluateGameOptions_Strait() {
                dice().getDiceSetMap().clear();
                for (int face = 1; face <= FULL_SET_OF_DICE; face++) {
                        dice().getDiceSetMap().put(face, 1);
                }
                dice().calculateNumDiceInPlay();

                List<GameOption> options = evaluateOptions();

                assertEquals(3, options.size(), "Strait plus single 1 and 5 should be available");
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.STRAIT));
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.SINGLE && o.value() == 1));
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.SINGLE && o.value() == 5));
        }

        @Test
        void testEvaluateGameOptions_AddMultiple_1() {
                setDice(Map.of(1, 3, 4, 1, 6, 2));

                List<GameOption> options = evaluateOptions();
                assertEquals(2, options.size(), "Multiple (1) and Single 1 should be available");
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.MULTIPLE && o.value() == 1));
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.SINGLE && o.value() == 1));

                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 1));
                assertTrue(gameOptionManager.isValidMove());
                gameOptionManager.processMove();

                assertEquals(1000, playerManager.getCurrentPlayer().score().getRoundScore());
                assertEquals(1, gameCoordinator.getRuleManager().getPreviouslySelectedMultipleValue());

                setDice(Map.of(1, 2));
                options = evaluateOptions();
                assertEquals(1, options.size(), "Add Multiple (1) should be the only option");
                assertEquals(GameOption.Type.ADD_MULTIPLE, options.get(0).type());
                assertEquals(1, options.get(0).value());
        }

        @Test
        void testEvaluateGameOptions_AddMultiple_3() {
                setDice(Map.of(3, 3, 4, 1, 6, 2));

                List<GameOption> options = evaluateOptions();
                assertEquals(1, options.size(), "Only Multiple (3) should be available");
                assertEquals(GameOption.Type.MULTIPLE, options.get(0).type());
                assertEquals(3, options.get(0).value());

                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 3));
                assertTrue(gameOptionManager.isValidMove());
                gameOptionManager.processMove();

                setDice(Map.of(3, 2));
                options = evaluateOptions();
                assertEquals(1, options.size(), "Add Multiple (3) should now be available");
                assertEquals(GameOption.Type.ADD_MULTIPLE, options.get(0).type());
                assertEquals(3, options.get(0).value());
        }

        @Test
        void testEvaluateGameOptions_Set() {
                setDice(Map.of(1, 2, 2, 2, 3, 2));

                List<GameOption> options = evaluateOptions();

                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.SET));
                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.SINGLE && o.value() == 1));
        }

        @Test
        void testEvaluateGameOptions_Multiple() {
                setDice(Map.of(3, 4));

                List<GameOption> options = evaluateOptions();

                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.MULTIPLE && o.value() == 3));
        }

        @Test
        void testEvaluateGameOptions_Single() {
                setDice(Map.of(5, 1));

                List<GameOption> options = evaluateOptions();

                assertTrue(options.stream().anyMatch(o -> o.type() == GameOption.Type.SINGLE && o.value() == 5));
        }

        @Test
        void testProcessMove_StraitScoresAndRemovesDice() {
                dice().getDiceSetMap().clear();
                for (int i = 1; i <= 6; i++) {
                        dice().getDiceSetMap().put(i, 1);
                }
                dice().calculateNumDiceInPlay();

                evaluateOptions();
                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.STRAIT, null));
                assertTrue(gameOptionManager.isValidMove());

                gameOptionManager.processMove();

                Player player = playerManager.getCurrentPlayer();
                assertEquals(1000, player.score().getRoundScore());
                assertEquals(0, player.dice().getNumDiceInPlay());
        }

        @Test
        void testProcessMove_Set() {
                setDice(Map.of(1, 2, 2, 2, 3, 2));

                evaluateOptions();
                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.SET, null));
                assertTrue(gameOptionManager.isValidMove());

                gameOptionManager.processMove();

                Player player = playerManager.getCurrentPlayer();
                assertEquals(1000, player.score().getRoundScore());
                assertEquals(0, player.dice().getNumDiceInPlay());
        }

        @Test
        void testProcessMove_Multiple() {
                setDice(Map.of(3, 3));

                evaluateOptions();
                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 3));
                assertTrue(gameOptionManager.isValidMove());

                gameOptionManager.processMove();

                Player player = playerManager.getCurrentPlayer();
                assertEquals(300, player.score().getRoundScore());
                assertEquals(0, player.dice().getNumDiceInPlay());
        }

        @Test
        void testProcessMove_Single() {
                setDice(Map.of(5, 1));

                evaluateOptions();
                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.SINGLE, 5));
                assertTrue(gameOptionManager.isValidMove());

                gameOptionManager.processMove();

                Player player = playerManager.getCurrentPlayer();
                assertEquals(50, player.score().getRoundScore());
                assertEquals(0, player.dice().getNumDiceInPlay());
        }

        @Test
        void testProcessMove_NoOptionSelected() {
                Player player = playerManager.getCurrentPlayer();
                player.score().setRoundScore(123);

                gameOptionManager.processMove();

                assertEquals(123, player.score().getRoundScore());
        }

        @Test
        void testProcessMove_InvalidMove() {
                setDice(Map.of(3, 1));

                evaluateOptions();
                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 3));

                assertFalse(gameOptionManager.isValidMove());
                gameOptionManager.processMove();

                assertEquals(0, playerManager.getCurrentPlayer().score().getRoundScore());
        }

        @Test
        void testAddMultipleRule() {
                setDice(Map.of(1, 3));
                evaluateOptions();

                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.MULTIPLE, 1));
                gameOptionManager.processMove();

                Player player = playerManager.getCurrentPlayer();
                assertEquals(1000, player.score().getRoundScore());
                assertEquals(1, gameCoordinator.getRuleManager().getPreviouslySelectedMultipleValue());

                setDice(Map.of(1, 2));
                List<GameOption> options = evaluateOptions();
                assertTrue(options.stream()
                                   .anyMatch(o -> o.type() == GameOption.Type.ADD_MULTIPLE && o.value() == 1));

                gameOptionManager.setSelectedGameOption(new GameOption(GameOption.Type.ADD_MULTIPLE, 1));
                assertTrue(gameOptionManager.isValidMove());
                gameOptionManager.processMove();

                assertEquals(4000, player.score().getRoundScore());
        }
}
