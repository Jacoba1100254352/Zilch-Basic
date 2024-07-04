package rules.managers;


import rules.models.IRuleStrategy;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;


public class RuleRegistry implements IRuleRegistry
{
	private final Map<RuleType, IRuleStrategy> rules = new EnumMap<>(RuleType.class);
	
	public RuleRegistry() {
		ServiceLoader<IRuleStrategy> loader = ServiceLoader.load(IRuleStrategy.class);
		for (IRuleStrategy rule : loader) {
			rules.put(RuleType.valueOf(rule.getClass().getSimpleName().toUpperCase()), rule);
		}
	}
	
	@Override
	public void configureRules(Map<RuleType, Object> config) {
		for (IRuleStrategy rule : rules.values()) {
			rule.configure(config);
		}
	}
	
	@Override
	public IRuleStrategy getRule(RuleType ruleType) {
		return rules.get(ruleType);
	}
	
	@Override
	public Map<RuleType, Object> getDefaultConfig() {
		Map<RuleType, Object> defaultConfig = new HashMap<>();
		for (IRuleStrategy rule : rules.values()) {
			defaultConfig.putAll(rule.getDefaultConfig());
		}
		return defaultConfig;
	}
}
