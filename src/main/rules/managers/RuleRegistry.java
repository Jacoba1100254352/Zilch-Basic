package rules.managers;


import rules.models.IRule;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


public class RuleRegistry implements IRuleRegistry
{
	private final Map<RuleType, IRule> rules = new EnumMap<>(RuleType.class);
	
	public RuleRegistry() {
		ServiceLoader<IRule> loader = ServiceLoader.load(IRule.class);
		for (IRule rule : loader) {
			rules.put(RuleType.valueOf(rule.getClass().getSimpleName().toUpperCase()), rule);
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
