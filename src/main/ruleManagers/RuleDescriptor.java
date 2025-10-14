package ruleManagers;

/**
 * Describes the metadata for a rule that can be shown to players for configuration.
 *
 * @param id      unique identifier for the rule.
 * @param name    human-friendly rule name.
 * @param enabled whether the rule is currently enabled.
 */
public record RuleDescriptor(String id, String name, boolean enabled) {}
