package models;


/**
 * Represents a selectable scoring option that is produced by an enabled {@link ruleManagers.RuleStrategy}.
 *
 * @param ruleId      Identifier of the rule that produced the option. Used to delegate the processing of the option.
 * @param type        High level category of the option. Maintained for backwards compatibility with previous logic.
 * @param value       Optional numeric value attached to an option (e.g. the face value of a multiple).
 * @param description Human readable description presented to the player.
 */
public record GameOption(String ruleId, GameOption.Type type, Integer value, String description)
{

        public GameOption
        {
                if (ruleId == null || ruleId.isBlank()) {
                        throw new IllegalArgumentException("ruleId must not be null or blank");
                }
        }

        public GameOption(String ruleId, GameOption.Type type, Integer value)
        {
                this(ruleId, type, value, null);
        }

        public enum Type
        {
                STRAIT, SET, MULTIPLE, ADD_MULTIPLE, SINGLE
        }
}
