package ruleManagers;


import managers.GameCoordinator;
import models.GameOption;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class RuleManager
{
        private static final String RULES_PACKAGE = "ruleManagers.rules";

        private final GameCoordinator gameCoordinator;
        private final Map<String, Rule> rulesById;
        private final Map<String, Boolean> enabledRules;
        private final Map<GameOption, Rule> optionRuleIndex;
        private List<GameOption> lastEvaluatedOptions;

        public RuleManager(GameCoordinator gameCoordinator) {
                this.gameCoordinator = gameCoordinator;
                this.rulesById = loadRules();
                this.enabledRules = new LinkedHashMap<>();
                for (Rule rule : rulesById.values()) {
                        enabledRules.put(rule.id(), true);
                }
                this.optionRuleIndex = new HashMap<>();
                this.lastEvaluatedOptions = List.of();
        }

        private Map<String, Rule> loadRules() {
                List<Rule> discoveredRules = new ArrayList<>(RuleLoader.discoverRules(RULES_PACKAGE));
                discoveredRules.sort(Comparator.comparing(Rule::displayName));
                Map<String, Rule> map = new LinkedHashMap<>();
                for (Rule rule : discoveredRules) {
                        map.put(rule.id(), rule);
                }
                return map;
        }

        public List<GameOption> evaluateOptions(RuleContext context) {
                optionRuleIndex.clear();
                List<GameOption> options = new ArrayList<>();
                for (Rule rule : rulesById.values()) {
                        if (!isRuleEnabled(rule.id())) {
                                continue;
                        }
                        List<GameOption> ruleOptions = rule.evaluateOptions(context);
                        for (GameOption option : ruleOptions) {
                                options.add(option);
                                optionRuleIndex.put(option, rule);
                        }
                }
                lastEvaluatedOptions = Collections.unmodifiableList(new ArrayList<>(options));
                return lastEvaluatedOptions;
        }

        public void applyOption(RuleContext context, GameOption option) {
                Rule rule = optionRuleIndex.get(option);
                if (rule == null) {
                        throw new IllegalArgumentException("No rule registered for option " + option);
                }
                rule.apply(context, option);
        }

        public String describe(GameOption option) {
                Rule rule = optionRuleIndex.get(option);
                if (rule == null) {
                        return option.type().name();
                }
                return rule.describe(option);
        }

        public boolean isOptionAvailable() {
                return !lastEvaluatedOptions.isEmpty();
        }

        public void setRuleEnabled(String ruleId, boolean enabled) {
                if (!rulesById.containsKey(ruleId)) {
                        throw new IllegalArgumentException("Unknown rule id: " + ruleId);
                }
                enabledRules.put(ruleId, enabled);
        }

        public boolean isRuleEnabled(String ruleId) {
                return enabledRules.getOrDefault(ruleId, false);
        }

        public List<Rule> getAllRules() {
                return List.copyOf(rulesById.values());
        }

        public Map<String, Boolean> getRuleStates() {
                return Collections.unmodifiableMap(enabledRules);
        }
}
