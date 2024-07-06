package rules.managers;


import rules.constantModels.IConstantRule;
import rules.variableModels.IRule;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


public class RuleRegistry implements IRuleRegistry
{
	private final Map<RuleType, IRule> rules = new EnumMap<>(RuleType.class);
	private final Map<RuleType, IConstantRule> constantRules = new EnumMap<>(RuleType.class);
	
	public RuleRegistry() {
		loadRules(IRule.class);
		loadRules(IConstantRule.class);
	}
	
	private <T> void loadRules(Class<T> service) {
		ServiceLoader<T> loader = ServiceLoader.load(service);
		for (T rule : loader) {
			if (rule instanceof IRule) {
				rules.put(((IRule) rule).getRuleType(), (IRule) rule);
			} else if (rule instanceof IConstantRule) {
				constantRules.put(((IConstantRule) rule).getRuleType(), (IConstantRule) rule);
			}
		}
	}
	
	@Override
	public void configureRules(Map<RuleType, Object> config) {
		for (IRule rule : rules.values()) {
			rule.configure(config);
		}
	}
	
	@Override
	public IRule getRule(RuleType ruleType) {
		return rules.get(ruleType);
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		for (IRule rule : rules.values()) {
			defaultConfig.putAll(rule.getDefaultConfig());
		}
		return defaultConfig;
	}
}
