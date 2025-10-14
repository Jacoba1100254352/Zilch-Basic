package ruleManagers;

import managers.GameCoordinator;
import models.GameOption;
import ruleManagers.rules.Rule;
import ruleManagers.rules.RuleContext;
import ruleManagers.rules.RuleLoader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class RuleManager {
    private static final String RULE_PACKAGE = "ruleManagers.rules";

    private final GameCoordinator gameCoordinator;
    private final RuleContext context;
    private final Map<String, RuleEntry> ruleEntries;
    private final Map<GameOption, RuleEntry> evaluatedOptions;

    public RuleManager(GameCoordinator gameCoordinator) {
        this.gameCoordinator = gameCoordinator;
        this.context = new RuleContext(gameCoordinator);
        this.ruleEntries = new LinkedHashMap<>();
        this.evaluatedOptions = new LinkedHashMap<>();
        loadRules();
    }

    private void loadRules() {
        List<Rule> rules = RuleLoader.loadRules(RULE_PACKAGE);
        rules.sort((r1, r2) -> {
            int priorityComparison = Integer.compare(r1.getPriority(), r2.getPriority());
            if (priorityComparison != 0) {
                return priorityComparison;
            }
            return r1.getId().compareTo(r2.getId());
        });

        for (Rule rule : rules) {
            RuleEntry entry = new RuleEntry(rule, rule.isEnabledByDefault());
            ruleEntries.put(rule.getId(), entry);
        }
    }

    public List<String> getRegisteredRuleIds() {
        return new ArrayList<>(ruleEntries.keySet());
    }

    public Map<String, Boolean> getRuleStates() {
        return ruleEntries.entrySet()
                           .stream()
                           .collect(Collectors.toMap(Map.Entry::getKey,
                                   entry -> entry.getValue().isEnabled(),
                                   (a, b) -> a,
                                   LinkedHashMap::new));
    }

    public void setRuleEnabled(String ruleId, boolean enabled) {
        RuleEntry entry = ruleEntries.get(ruleId);
        if (entry != null) {
            entry.setEnabled(enabled);
        }
    }

    public void setRuleEnabled(Class<? extends Rule> ruleClass, boolean enabled) {
        setRuleEnabled(ruleClass.getSimpleName(), enabled);
    }

    public boolean isRuleEnabled(String ruleId) {
        RuleEntry entry = ruleEntries.get(ruleId);
        return entry != null && entry.isEnabled();
    }

    public boolean isRuleEnabled(Class<? extends Rule> ruleClass) {
        return isRuleEnabled(ruleClass.getSimpleName());
    }

    public List<GameOption> evaluateAvailableOptions() {
        evaluatedOptions.clear();
        List<GameOption> options = new ArrayList<>();
        for (RuleEntry entry : ruleEntries.values()) {
            if (!entry.isEnabled()) {
                continue;
            }

            Collection<GameOption> ruleOptions = entry.getRule().evaluate(context);
            List<GameOption> sortedOptions = ruleOptions.stream()
                                                        .sorted(entry.getRule().getOptionComparator())
                                                        .collect(Collectors.toList());
            for (GameOption option : sortedOptions) {
                options.add(option);
                evaluatedOptions.put(option, entry);
            }
        }
        return Collections.unmodifiableList(options);
    }

    public boolean isOptionAvailable() {
        if (evaluatedOptions.isEmpty()) {
            evaluateAvailableOptions();
        }
        return !evaluatedOptions.isEmpty();
    }

    public boolean isRuleValid(GameOption option) {
        return evaluatedOptions.containsKey(option);
    }

    public void apply(GameOption option) {
        RuleEntry entry = evaluatedOptions.get(option);
        if (entry == null) {
            throw new IllegalArgumentException("No rule available for option: " + option);
        }
        entry.getRule().apply(option, context);
    }

    public Optional<Rule> getRule(String ruleId) {
        return Optional.ofNullable(ruleEntries.get(ruleId)).map(RuleEntry::getRule);
    }

    public Integer getPreviouslySelectedMultipleValue() {
        return context.getPreviouslySelectedMultipleValue();
    }

    public void setPreviouslySelectedMultipleValue(Integer value) {
        context.setPreviouslySelectedMultipleValue(value);
    }

    public void clearPreviouslySelectedMultipleValue() {
        context.clearPreviouslySelectedMultipleValue();
    }

    public RuleContext getContext() {
        return context;
    }

    public GameCoordinator getGameCoordinator() {
        return gameCoordinator;
    }

    private static final class RuleEntry {
        private final Rule rule;
        private boolean enabled;

        private RuleEntry(Rule rule, boolean enabled) {
            this.rule = Objects.requireNonNull(rule);
            this.enabled = enabled;
        }

        public Rule getRule() {
            return rule;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }
    }
}
