package enterprises.iwakura.jdainteractables;

import java.util.function.Function;

import enterprises.iwakura.jdainteractables.InteractionRule.Result;

/**
 * A firewall rule that takes a {@link InteractionEventContext} and returns a boolean indicating whether the interaction is
 * allowed.
 */
@FunctionalInterface
public interface InteractionRule extends Function<InteractionEventContext, Result> {

    enum Result {
        /**
         * Allows the interaction to proceed. Won't process any further rules.
         */
        ALLOW,
        /**
         * Denies the interaction. Won't process any further rules.
         */
        DENY,
        /**
         * Does not affect the interaction decision. Further rules will be processed.
         */
        NEUTRAL;
    }
}
