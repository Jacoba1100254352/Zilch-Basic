package rules.managers;


import java.util.Locale;
import java.util.Objects;


/**
 * Value object used to identify rules without relying on a central enum list.
 * This keeps new rule classes self-contained: a rule can declare its own id
 * and be discovered without editing shared registration code.
 */
public final class RuleType implements Comparable<RuleType>
{
	public static final RuleType ADD_MULTIPLE = new RuleType("add_multiple");
	public static final RuleType MULTIPLE = new RuleType("multiple");
	public static final RuleType SINGLE = new RuleType("single");
	public static final RuleType SET = new RuleType("set");
	public static final RuleType STRAIT = new RuleType("strait");
	public static final RuleType ROLL_AGAIN = new RuleType("roll_again");
	public static final RuleType END_TURN = new RuleType("end_turn");
	public static final RuleType FIRST_ROLL_BUST = new RuleType("first_roll_bust");
	public static final RuleType STEALING = new RuleType("stealing");

	private final String id;

	/**
	 * Creates a normalized rule identifier.
	 *
	 * @param id The rule id, typically lower_snake_case.
	 */
	public RuleType(String id) {
		if (id == null || id.isBlank()) {
			throw new IllegalArgumentException("Rule id cannot be blank.");
		}
		this.id = id.trim().toLowerCase(Locale.ROOT);
	}

	public String id() {
		return id;
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof RuleType ruleType)) {
			return false;
		}
		return id.equals(ruleType.id);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public int compareTo(RuleType other) {
		return this.id.compareTo(other.id);
	}

	@Override
	public String toString() {
		return id;
	}
}
