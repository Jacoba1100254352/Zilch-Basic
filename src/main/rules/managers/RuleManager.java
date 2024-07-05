package rules.managers;


import model.entities.GameOption;
import model.entities.Player;
import model.managers.ActionManager;
import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.models.IRule;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class RuleManager implements IRuleManager
{
	private final IRuleRegistry ruleRegistry;
	
	public RuleManager(IRuleRegistry ruleRegistry) {
		this.ruleRegistry = ruleRegistry;
	}
	
	@Override
	public void initializeRules(Map<RuleType, Object> config) {
		ruleRegistry.configureRules(config);
	}
	
	@Override
	public List<GameOption> evaluateRules(Player player, int numOptionsSelected) {
		List<GameOption> gameOptions = new ArrayList<>();
		Map<Integer, Integer> diceSetMap = player.dice().diceSetMap();
		
		for (RuleType ruleType : RuleType.values()) {
			IRule rule = ruleRegistry.getRule(ruleType);
			if (rule != null && rule.isValid(new RuleContext(diceSetMap))) {
				// FIXME: This will need to change depending on the rule
				// ... Maybe have some logic elsewhere that determines based on if the user provided a value what to put
				// OR default to providing all the values for now
				gameOptions.add(new GameOption(ruleType, null, rule.getDescription()));
			}
		}
		return gameOptions;
	}
	
	@Override
	public IRule getRule(RuleType ruleType) {
		return ruleRegistry.getRule(ruleType);
	}
	
	@Override
	public void applyRule(ActionManager actionManager, GameOption option, Player player) {
		IRule rule = getRule(option.type());
		if (rule != null) {
			rule.score(new ScoreContext(player.score())); // FIXME: This will need to change depending on the rule
		}
	}
}
