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
        ALLOW,
        DENY,
        NEUTRAL;
    }
}
