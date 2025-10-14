package ruleManagers;

import managers.GameCoordinator;
import modelManagers.PlayerManager;
import models.GameOption;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Coordinates rule discovery, configuration, and execution.
 */
public class RuleManager
{
        private final GameCoordinator gameCoordinator;
        private final Map<String, Rule> rulesById;
        private final Set<String> activeRuleIds;
        private final Map<GameOption, Rule> optionRuleMap;
        private Integer previouslySelectedMultipleValue;

        public RuleManager(GameCoordinator gameCoordinator) {
                this.gameCoordinator = gameCoordinator;
                this.rulesById = loadRules();
                this.activeRuleIds = rulesById.values().stream()
                                              .filter(Rule::enabledByDefault)
                                              .map(Rule::id)
                                              .collect(Collectors.toCollection(LinkedHashSet::new));
                this.optionRuleMap = new LinkedHashMap<>();
                this.previouslySelectedMultipleValue = null;
        }

        private Map<String, Rule> loadRules() {
                List<Rule> discovered = RuleDiscovery.discoverRules();
                if (discovered.isEmpty()) {
                        throw new IllegalStateException("No rule implementations were discovered.");
                }
                discovered.sort((left, right) -> left.displayName().compareToIgnoreCase(right.displayName()));
                Map<String, Rule> map = new LinkedHashMap<>();
                for (Rule rule : discovered) {
                        map.put(rule.id(), rule);
                }
                return map;
        }

        public List<RuleDescriptor> getAvailableRuleDescriptors() {
                return rulesById.values().stream()
                                .map(rule -> new RuleDescriptor(rule.id(), rule.displayName(), activeRuleIds.contains(rule.id())))
                                .toList();
        }

        public void configureActiveRules(List<String> enabledRuleIds) {
                if (enabledRuleIds == null || enabledRuleIds.isEmpty()) {
                        return;
                }
                List<String> sanitized = enabledRuleIds.stream()
                                                        .filter(rulesById::containsKey)
                                                        .toList();
                if (sanitized.isEmpty()) {
                        return;
                }
                activeRuleIds.clear();
                activeRuleIds.addAll(sanitized);
                optionRuleMap.clear();
        }

        public List<GameOption> evaluateAvailableOptions(Map<Integer, Integer> diceSetMap) {
                RuleContext context = new RuleContext(gameCoordinator, this, diceSetMap);
                optionRuleMap.clear();
                List<GameOption> options = new ArrayList<>();
                for (String ruleId : activeRuleIds) {
                        Rule rule = rulesById.get(ruleId);
                        if (rule == null) {
                                continue;
                        }
                        List<GameOption> generated = rule.evaluate(context);
                        for (GameOption option : generated) {
                                optionRuleMap.put(option, rule);
                                options.add(option);
                        }
                }
                return options;
        }

        public boolean isOptionAvailable() {
                if (!optionRuleMap.isEmpty()) {
                        return true;
                }
                PlayerManager playerManager = gameCoordinator.getPlayerManager();
                if (playerManager == null || playerManager.getCurrentPlayer() == null) {
                        return false;
                }
                Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
                return !evaluateAvailableOptions(diceSetMap).isEmpty();
        }

        public void apply(GameOption option) {
                Rule rule = optionRuleMap.get(option);
                if (rule == null) {
                        gameCoordinator.getGameplayUI().displayMessage("Invalid move selected.");
                        return;
                }
                PlayerManager playerManager = gameCoordinator.getPlayerManager();
                Map<Integer, Integer> diceSetMap = playerManager.getDice(playerManager.getCurrentPlayer());
                RuleContext context = new RuleContext(gameCoordinator, this, diceSetMap);
                rule.apply(option, context);
                optionRuleMap.clear();
        }

        public void resetTurnState() {
                previouslySelectedMultipleValue = null;
        }

        public Integer getPreviouslySelectedMultipleValue() {
                return previouslySelectedMultipleValue;
        }

        public void setPreviouslySelectedMultipleValue(Integer value) {
                previouslySelectedMultipleValue = value;
        }
}
