package ruleManagers.rules;

import models.GameOption;

import java.util.Collection;
import java.util.Comparator;

public interface Rule {
    default String getId() {
        return getClass().getSimpleName();
    }

    GameOption.Type getType();

    Collection<GameOption> evaluate(RuleContext context);

    void apply(GameOption option, RuleContext context);

    default int getPriority() {
        return 100;
    }

    default Comparator<GameOption> getOptionComparator() {
        return Comparator
                .comparing((GameOption option) -> option.value(), Comparator.nullsFirst(Integer::compareTo));
    }

    default boolean supports(GameOption option) {
        return option != null && option.type() == getType();
    }

    default boolean isEnabledByDefault() {
        return true;
    }
}
