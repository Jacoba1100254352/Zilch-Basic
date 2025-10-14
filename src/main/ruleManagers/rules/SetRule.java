package ruleManagers.rules;

import models.GameOption;
import ruleManagers.Rule;
import ruleManagers.RuleContext;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SetRule implements Rule
{
        @Override
        public String id() {
                return "set";
        }

        @Override
        public String displayName() {
                return "Sets";
        }

        @Override
        public List<GameOption> evaluateOptions(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                if (diceSetMap.size() == 3 && diceSetMap.values().stream().allMatch(count -> count == 2)) {
                        return List.of(new GameOption(GameOption.Type.SET, null));
                }
                return Collections.emptyList();
        }

        @Override
        public void apply(RuleContext context, GameOption option) {
                context.getPlayerManager().scoreSets();
                context.getPlayerManager().removeAllDice();
                context.getGameOptionManager().setPreviouslySelectedMultipleValue(null);
        }

        @Override
        public String describe(GameOption option) {
                return "Score a Set";
        }
}
