package enterprises.iwakura.jdainteractables;

import java.util.function.Function;

import enterprises.iwakura.jdainteractables.InteractionRule.Result;

/**
 * A firewall rule that takes a {@link UserContext} and returns a boolean indicating whether the interaction is
 * allowed.
 */
@FunctionalInterface
public interface InteractionRule extends Function<UserContext, Result> {

    enum Result {
        ALLOW,
        DENY,
        NEUTRAL;
    }
}
