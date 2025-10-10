package enterprises.iwakura.jdainteractables;

import java.util.function.Consumer;

/**
 * A callback that is called when an interaction is denied by the firewall rules. It takes an
 * {@link InteractionEventContext} as input.
 */
@FunctionalInterface
public interface InteractionDeniedCallback extends Consumer<InteractionEventContext> {

}
