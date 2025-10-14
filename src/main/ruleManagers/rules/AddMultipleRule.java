package ruleManagers.rules;

import models.GameOption;
import ruleManagers.RuleContext;

import java.util.List;
import java.util.Map;

/**
 * Allows a player to continue scoring multiples with additional dice in subsequent rolls.
 */
public class AddMultipleRule extends AbstractRule
{
        public AddMultipleRule() {
                super("add-multiple", "Add Multiples", GameOption.Type.ADD_MULTIPLE);
        }

        @Override
        public List<GameOption> evaluate(RuleContext context) {
                Integer previouslySelected = context.getPreviouslySelectedMultipleValue();
                if (previouslySelected == null) {
                        return List.of();
                }

                Map<Integer, Integer> diceSetMap = context.getDiceSetMap();
                int diceInPlay = diceSetMap.values().stream().mapToInt(Integer::intValue).sum();
                if (diceInPlay > 3) {
                        return List.of();
                }

                if (diceSetMap.getOrDefault(previouslySelected, 0) > 0) {
                        return List.of(new GameOption(optionType(), previouslySelected));
                }
                return List.of();
        }

        @Override
        public void apply(GameOption option, RuleContext context) {
                int dieValue = option.value();
                context.getPlayerManager().scoreMultiple(dieValue);
                context.getPlayerManager().eliminateDice(dieValue);
        }
}
