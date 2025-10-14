package ruleManagers.rules;

import models.GameOption;
import ruleManagers.Rule;

/**
 * Base class for concrete rules providing standard metadata handling.
 */
public abstract class AbstractRule implements Rule
{
        private final String id;
        private final String displayName;
        private final GameOption.Type optionType;
        private final boolean enabledByDefault;

        protected AbstractRule(String id, String displayName, GameOption.Type optionType) {
                this(id, displayName, optionType, true);
        }

        protected AbstractRule(String id, String displayName, GameOption.Type optionType, boolean enabledByDefault) {
                this.id = id;
                this.displayName = displayName;
                this.optionType = optionType;
                this.enabledByDefault = enabledByDefault;
        }

        @Override
        public String id() {
                return id;
        }

        @Override
        public String displayName() {
                return displayName;
        }

        @Override
        public boolean enabledByDefault() {
                return enabledByDefault;
        }

        @Override
        public GameOption.Type optionType() {
                return optionType;
        }
}
