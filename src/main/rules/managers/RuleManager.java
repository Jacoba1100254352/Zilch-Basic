package rules.managers;

import model.entities.GameOption;
import model.entities.Player;
import rules.constantModels.IConstantRule;
import rules.context.RuleContext;
import rules.context.ScoreContext;
import rules.variableModels.IRule;
import rules.variableModels.IVariableRule;

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
	public List<GameOption> evaluateRules(Map<Integer, Integer> diceSetMap) {
		List<GameOption> gameOptions = new ArrayList<>();
		
		for (RuleType ruleType : RuleType.values()) {
			IRule rule = ruleRegistry.getRule(ruleType);
			if (rule instanceof IVariableRule variableRule) {
				if (variableRule.isValid(new RuleContext(diceSetMap))) {
					gameOptions.add(new GameOption(ruleType, null, rule.getDescription()));
				}
			}
			if (rule instanceof IConstantRule constantRule) {
				if (constantRule.isValid(null, null)) { // Replace with actual values if needed
					gameOptions.add(new GameOption(ruleType, null, rule.getDescription()));
				}
			}
		}
		return gameOptions;
	}
	
	@Override
	public IRule getRule(RuleType ruleType) {
		return ruleRegistry.getRule(ruleType);
	}
	
	@Override
	public void applyRule(Player player, GameOption option) {
		IRule rule = getRule(option.type());
		if (rule instanceof IVariableRule variableRule) {
			variableRule.score(new ScoreContext(player.score(), option.value(), player.dice().diceSetMap().get(option.value())));
		} else if (rule instanceof IConstantRule constantRule) {
			constantRule.applyAction();
		}
	}
}
