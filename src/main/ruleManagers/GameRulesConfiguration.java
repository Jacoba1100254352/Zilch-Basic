package ruleManagers;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Holds which rules are enabled for the current game session.
 */
public class GameRulesConfiguration
{
        private final Set<String> enabledRules = new LinkedHashSet<>();

        public void enable(String ruleId)
        {
                enabledRules.add(ruleId);
        }

        public void disable(String ruleId)
        {
                enabledRules.remove(ruleId);
        }

        public void setEnabled(String ruleId, boolean enabled)
        {
                if (enabled) {
                        enable(ruleId);
                } else {
                        disable(ruleId);
                }
        }

        public boolean isEnabled(String ruleId)
        {
                return enabledRules.contains(ruleId);
        }

        public Set<String> enabledRules()
        {
                return Collections.unmodifiableSet(enabledRules);
        }

        public void enableAll(Collection<String> ruleIds)
        {
                enabledRules.addAll(ruleIds);
        }
}
