package enterprises.iwakura.jdainteractables;

import java.util.function.BiFunction;

import enterprises.iwakura.jdainteractables.InteractionHandler.Result;
import enterprises.iwakura.jdainteractables.components.Interactable;
import enterprises.iwakura.jdainteractables.components.InteractableMessage;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * Interaction handler that processes an interaction event invoked by User on an interactable component.
 *
 * @param <T> Type of the interactable component, e.g. {@link InteractableMessage}
 * @param <E> Type of the interaction event, e.g. {@link ButtonInteractionEvent}, {@link StringSelectInteractionEvent},
 *            etc.
 */
public interface InteractionHandler<T extends Interactable<?>, E> extends BiFunction<T, E, Result> {

    enum Result {
        /**
         * Removes the interaction from {@link InteractableListener}, making the message non-interactable for the user
         * until updated by another {@link InteractableMessage}
         */
        REMOVE,

        /**
         * Keeps the interaction in {@link InteractableListener}, allowing the user to interact again
         */
        KEEP,

        /**
         * Does nothing, allowing other interactables to be processed. This is used internally. Please, use
         * {@link #KEEP} or {@link #REMOVE} instead.
         */
        NOT_PROCESSED,

        /**
         * Ignores the interaction, preventing other interactables from being processed. This is used internally.
         * Please, use {@link #KEEP} or {@link #REMOVE} instead.
         */
        IGNORE;
    }
}
