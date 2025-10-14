package ruleManagers;

import managers.GameCoordinator;
import models.GameOption;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Coordinates rule strategies and delegates scoring logic.
 */
public class RuleManager
{
        private final GameCoordinator gameCoordinator;
        private final RuleRegistry ruleRegistry;
        private final GameRulesConfiguration configuration;

        public RuleManager(GameCoordinator gameCoordinator)
        {
                this(gameCoordinator, new RuleRegistry(), new GameRulesConfiguration());
        }

        RuleManager(GameCoordinator gameCoordinator, RuleRegistry ruleRegistry, GameRulesConfiguration configuration)
        {
                this.gameCoordinator = gameCoordinator;
                this.ruleRegistry = ruleRegistry;
                this.configuration = configuration;
                configuration.enableAll(ruleRegistry.strategies()
                                                  .stream()
                                                  .map(RuleStrategy::id)
                                                  .collect(Collectors.toCollection(LinkedHashSet::new)));
        }

        public List<GameOption> evaluateAvailableOptions()
        {
                RuleContext context = new RuleContext(gameCoordinator);
                List<GameOption> options = new ArrayList<>();
                ruleRegistry.strategies()
                            .stream()
                            .filter(strategy -> configuration.isEnabled(strategy.id()))
                            .sorted(Comparator.comparingInt(RuleStrategy::priority).thenComparing(RuleStrategy::displayName))
                            .forEach(strategy -> options.addAll(strategy.evaluateOptions(context)));
                return options;
        }

        public boolean hasAvailableOptions()
        {
                return !evaluateAvailableOptions().isEmpty();
        }

        public void apply(GameOption option)
        {
                Objects.requireNonNull(option, "option must not be null");
                RuleStrategy strategy = ruleRegistry.findById(option.ruleId());
                if (strategy == null) {
                        throw new IllegalArgumentException("Unknown rule: " + option.ruleId());
                }
                if (!configuration.isEnabled(strategy.id())) {
                        throw new IllegalStateException("Rule is disabled: " + strategy.displayName());
                }
                strategy.apply(option, new RuleContext(gameCoordinator));
        }

        public List<RuleDescriptor> getRuleDescriptors()
        {
                return ruleRegistry.descriptors();
        }

        public void setRuleEnabled(String ruleId, boolean enabled)
        {
                configuration.setEnabled(ruleId, enabled);
        }

        public boolean isRuleEnabled(String ruleId)
        {
                return configuration.isEnabled(ruleId);
        }

        public GameRulesConfiguration configuration()
        {
                return configuration;
        }
}
