package enterprises.iwakura.jdainteractables.components;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import enterprises.iwakura.jdainteractables.Interaction;
import enterprises.iwakura.jdainteractables.InteractionEventContext;
import enterprises.iwakura.jdainteractables.InteractionHandler;
import enterprises.iwakura.jdainteractables.InteractionHandler.Result;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.selections.SelectOption;

/**
 * Represents an interactable message
 */
public class InteractableMessage extends Interactable<InteractableMessage> {

    protected final Map<Interaction<?, ?>, InteractionHandler<?>> interactions =
        Collections.synchronizedMap(new HashMap<>());

    /**
     * Adds an interaction to the interactable message
     *
     * @param interaction        The interaction to add
     * @param interactionHandler The handler to handle the interaction
     * @param <T>                The type of the component returned by the interaction
     * @return The component associated with the interaction
     * @throws IllegalArgumentException if the interaction already exists in this interactable message
     */
    public <T, E> T addInteraction(
        Interaction<T, E> interaction,
        InteractionHandler<E> interactionHandler
    ) {
        interactions.put(interaction, interactionHandler);
        return interaction.getComponent();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    public Result process(InteractionEventContext ctx) {
        if (super.process(ctx) == Result.IGNORE) {
            return Result.IGNORE;
        }

        Result result = Result.NOT_PROCESSED;

        for (var entry : interactions.entrySet()) {
            // Interaction<Component, Event>
            Interaction interaction = entry.getKey();
            // InteractionHandler<Event>
            InteractionHandler handler = entry.getValue();

            if (isApplicable(interaction, ctx)) {
                // #getInteraction() => Event
                result = (Result) handler.apply(ctx.getInteraction());
                // No breaking - allows multiple interactions to be processed
            }
        }

        return result;
    }

    /**
     * Determines if the interaction is applicable to this interactable message based on the interaction type and
     * context.
     *
     * @param interaction The interaction to check
     * @param ctx         The event context to check
     * @return true if applicable, false otherwise
     */
    @Override
    public boolean isApplicable(Interaction<?, ?> interaction, InteractionEventContext ctx) {
        if (super.isApplicable(interaction, ctx)) {
            if (!ctx.isModalInteraction()) {
                switch (ctx.getInteractionType()) {
                    case BUTTON_CLICK -> {
                        Button clickedButton = ctx.getButtonInteractionEvent().getButton();
                        Button interactionButton = interaction.getButton();

                        String clickedButtonId = clickedButton.getCustomId();
                        String interactionButtonId = interactionButton.getCustomId();

                        if (clickedButtonId != null && interactionButtonId != null) {
                            return clickedButtonId.equals(interactionButtonId);
                        }
                    }
                    case STRING_SELECT_MENU -> {
                        if (interaction.isSelectOption()) {
                            // Check for selected values in the interaction
                            String interactionValue = interaction.getSelectOption().getValue();
                            List<SelectOption> selectOptions = ctx.getStringSelectInteractionEvent()
                                .getInteraction()
                                .getSelectedOptions();
                            for (var selectedOption : selectOptions) {
                                if (interactionValue.equals(selectedOption.getValue())) {
                                    return true;
                                }
                            }
                        } else if (interaction.isStringSelectMenu()) {
                            // Check for entire select menu match (custom id)
                            String interactionId = interaction.getStringSelectMenu().getCustomId();
                            String selectedId = ctx.getStringSelectInteractionEvent()
                                .getInteraction()
                                .getCustomId();
                            return interactionId.equals(selectedId);
                        }
                    }
                    case ENTITY_SELECT_MENU -> {
                        String interactionId = interaction.getEntitySelectMenu().getCustomId();
                        String selectedId = ctx.getEntitySelectInteractionEvent()
                            .getInteraction()
                            .getCustomId();
                        return interactionId.equals(selectedId);
                    }
                }
            }
        }

        return false;
    }
}
