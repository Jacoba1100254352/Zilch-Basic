package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;

import java.util.List;
import java.util.Map;

/**
 * Awards points for rolling three pairs (a set).
 */
public class SetRule extends AbstractRule
{
        public SetRule() {
                super("set", "Sets", GameOption.Type.SET);
        }

        @Override
        public List<GameOption> evaluate(RuleContext context) {
                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                boolean allPairs = !diceSetMap.isEmpty() && diceSetMap.values().stream().allMatch(count -> count == 2);
                if (diceSetMap.size() == 3 && allPairs) {
                        return List.of(new GameOption(optionType(), null));
                }
                return List.of();
        }

        @Override
        public void apply(GameOption option, RuleContext context) {
                context.getPlayerManager().scoreSets();
                context.getPlayerManager().removeAllDice();
                context.clearPreviouslySelectedMultipleValue();
        }
}
