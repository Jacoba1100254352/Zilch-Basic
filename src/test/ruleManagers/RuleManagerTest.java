package ruleManagers;

import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.Dice;
import models.GameOption;
import models.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ruleManagers.rules.MultipleRuleStrategy;
import ruleManagers.rules.StraitRuleStrategy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static models.Dice.FULL_SET_OF_DICE;
import static org.junit.jupiter.api.Assertions.*;

class RuleManagerTest
{
        private GameCoordinator gameCoordinator;
        private RuleManager ruleManager;

        @BeforeEach
        void setUp()
        {
                gameCoordinator = new GameCoordinator();
                ruleManager = gameCoordinator.getRuleManager();
                gameCoordinator.setPlayerManager(new PlayerManager(List.of("Test"), 5000));
        }

        @Test
        void evaluateAvailableOptionsIncludesStrait()
        {
                Player player = gameCoordinator.getPlayerManager().getCurrentPlayer();
                Dice dice = player.dice();
                dice.getDiceSetMap().clear();
                for (int face = 1; face <= FULL_SET_OF_DICE; face++) {
                        dice.getDiceSetMap().put(face, 1);
                }
                dice.calculateNumDiceInPlay();

                List<GameOption> options = ruleManager.evaluateAvailableOptions();

                assertTrue(options.stream().anyMatch(option -> option.type() == GameOption.Type.STRAIT));
        }

        @Test
        void disablingRuleRemovesOptions()
        {
                Player player = gameCoordinator.getPlayerManager().getCurrentPlayer();
                Dice dice = player.dice();
                dice.getDiceSetMap().clear();
                for (int face = 1; face <= FULL_SET_OF_DICE; face++) {
                        dice.getDiceSetMap().put(face, 1);
                }
                dice.calculateNumDiceInPlay();

                ruleManager.setRuleEnabled(StraitRuleStrategy.ID, false);

                List<GameOption> options = ruleManager.evaluateAvailableOptions();

                assertTrue(options.stream().noneMatch(option -> option.ruleId().equals(StraitRuleStrategy.ID)));
        }

        @Test
        void applyMultipleUpdatesScoreAndDice()
        {
                Player player = gameCoordinator.getPlayerManager().getCurrentPlayer();
                Dice dice = player.dice();
                dice.setDiceSetMap(new HashMap<>(Map.of(3, 3)));
                dice.calculateNumDiceInPlay();

                List<GameOption> options = ruleManager.evaluateAvailableOptions();
                GameOption multipleThree = options.stream()
                                                  .filter(option -> option.ruleId().equals(MultipleRuleStrategy.ID))
                                                  .findFirst()
                                                  .orElseThrow();

                ruleManager.apply(multipleThree);

                assertEquals(300, player.score().getRoundScore());
                assertEquals(0, player.dice().getNumDiceInPlay());
                assertEquals(3, gameCoordinator.getGameOptionManager().getPreviouslySelectedMultipleValue());
        }
}
