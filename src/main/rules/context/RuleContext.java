package rules.context;


import model.entities.Player;

import java.util.Map;


public record RuleContext(Player player, Map<Integer, Integer> diceSetMap, Map<Integer, Integer> scoredMultiples)
{
	public RuleContext {
		if (player == null) {
			throw new IllegalArgumentException("player cannot be null.");
		}
		if (diceSetMap == null) {
			throw new IllegalArgumentException("diceSetMap cannot be null.");
		}
		if (scoredMultiples == null) {
			throw new IllegalArgumentException("scoredMultiples cannot be null.");
		}
	}
}
