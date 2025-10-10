package enterprises.iwakura.jdainteractables;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.callbacks.IDeferrableCallback;
import net.dv8tion.jda.api.interactions.callbacks.IMessageEditCallback;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;
import net.dv8tion.jda.api.interactions.components.ComponentInteraction;
import net.dv8tion.jda.api.interactions.modals.ModalInteraction;

/**
 * Context for {@link Interaction} received from JDA. Wraps JDA's {@link Interaction} and provides utility methods to
 * work with it.
 */
@Data
@RequiredArgsConstructor
public class InteractionEventContext {

    private final Interaction interaction;

    /**
     * Returns type of this interaction event.<br> This method DOES NOT return JDA's
     * {@link net.dv8tion.jda.api.interactions.InteractionType}!
     *
     * @return Non-null {@link InteractionType}
     */
    public InteractionType getInteractionType() {
        if (isButtonInteraction()) {
            return InteractionType.BUTTON_CLICK;
        }

        if (isStringSelectMenuInteraction()) {
            return InteractionType.STRING_SELECT_MENU;
        }

        if (isEntitySelectMenuInteraction()) {
            return InteractionType.ENTITY_SELECT_MENU;
        }

        if (isModalInteraction()) {
            return InteractionType.MODAL_SUBMITTED;
        }

        throw new IllegalStateException("Unknown interaction type");
    }

    /**
     * Determines if this interaction event is of type {@link InteractionType#BUTTON_CLICK}
     *
     * @return true if this interaction event is of type {@link InteractionType#BUTTON_CLICK}
     */
    public boolean isButtonInteraction() {
        return interaction instanceof ButtonInteractionEvent;
    }

    /**
     * Determines if this interaction event is of type {@link InteractionType#STRING_SELECT_MENU}
     *
     * @return true if this interaction event is of type {@link InteractionType#STRING_SELECT_MENU}
     */
    public boolean isStringSelectMenuInteraction() {
        return interaction instanceof StringSelectInteractionEvent;
    }

    /**
     * Determines if this interaction event is of type {@link InteractionType#ENTITY_SELECT_MENU}
     *
     * @return true if this interaction event is of type {@link InteractionType#ENTITY_SELECT_MENU}
     */
    public boolean isEntitySelectMenuInteraction() {
        return interaction instanceof EntitySelectInteractionEvent;
    }

    /**
     * Determines if this interaction event is of type {@link InteractionType#MODAL_SUBMITTED}
     *
     * @return true if this interaction event is of type {@link InteractionType#MODAL_SUBMITTED}
     */
    public boolean isModalInteraction() {
        return interaction instanceof ModalInteractionEvent;
    }

    /**
     * Gets {@link InteractionHook} from corresponding event
     *
     * @return Non-ull {@link InteractionHook}
     * @throws IllegalStateException if the interaction is not deferrable
     */
    public InteractionHook getInteractionHook() {
        if (!(interaction instanceof IDeferrableCallback)) {
            throw new IllegalStateException("Interaction is not deferrable");
        }

        return ((IDeferrableCallback) interaction).getHook();
    }

    /**
     * Returns Message ID of interacted message.
     *
     * @return Message ID of interacted message. If the interaction type is not of {@link ComponentInteraction} (e.g.
     * {@link InteractionType#MODAL_SUBMITTED}), an {@link IllegalStateException} is thrown
     * @throws IllegalStateException if the interaction is not of {@link ComponentInteraction}
     */
    public long getInteractedMessageId() {
        if (!(interaction instanceof ComponentInteraction)) {
            throw new IllegalStateException("Interaction is not a component interaction");
        }

        return ((ComponentInteraction) interaction).getMessageIdLong();
    }

    /**
     * Returns {@link Message} of interacted message.
     *
     * @return {@link Message}. If the interaction type is not of {@link ComponentInteraction} (e.g.
     * {@link InteractionType#MODAL_SUBMITTED}), an {@link IllegalStateException} is thrown
     * @throws IllegalStateException if the interaction is not of {@link ComponentInteraction}
     */
    public Message getInteractedMessage() {
        if (!(interaction instanceof ComponentInteraction)) {
            throw new IllegalStateException("Interaction is not a component interaction");
        }

        return ((ComponentInteraction) interaction).getMessage();
    }

    /**
     * Returns {@link MessageChannelUnion} of interacted message.
     *
     * @return {@link MessageChannelUnion}. If the interaction type is not of {@link ComponentInteraction} or
     * {@link ModalInteraction} (e.g. {@link InteractionType#UNKNOWN}), an {@link IllegalStateException} is thrown
     * @throws IllegalStateException if the interaction is not of {@link ComponentInteraction} or
     *                               {@link ModalInteraction}
     */
    public MessageChannelUnion getInteractedChannel() {
        if (interaction instanceof ComponentInteraction) {
            return ((ComponentInteraction) interaction).getChannel();
        }

        if (interaction instanceof ModalInteractionEvent) {
            return ((ModalInteraction) interaction).getChannel();
        }

        throw new IllegalStateException("Interaction is not a component or modal interaction");
    }

    /**
     * Returns {@link User} of interacted message
     *
     * @return {@link User}
     */
    public User getUser() {
        return interaction.getUser();
    }

    /**
     * Returns {@link Member} of interacted message. If the interaction is not from a guild, null is returned.
     *
     * @return {@link Member} or null if the interaction is not from a guild
     */
    public Member getMember() {
        return interaction.getMember();
    }

    /**
     * Returns {@link Guild} of interacted message. If the interaction is not from a guild, null is returned.
     *
     * @return {@link Guild} or null if the interaction is not from a guild
     */
    public Guild getGuild() {
        return interaction.getGuild();
    }

    /**
     * Casts interaction to {@link ButtonInteractionEvent}.<br> Throws {@link IllegalStateException} if the interaction
     * is not of type {@link InteractionType#BUTTON_CLICK}
     *
     * @return Non-null {@link ButtonInteractionEvent}
     * @throws IllegalStateException if the interaction is not of type {@link InteractionType#BUTTON_CLICK}
     */
    public ComponentInteraction getAsComponentInteraction() {
        if (!(interaction instanceof ComponentInteraction)) {
            throw new IllegalStateException("Interaction is not a component interaction");
        }

        return (ComponentInteraction) interaction;
    }

    /**
     * Casts interaction to {@link IDeferrableCallback}.<br> Throws {@link IllegalStateException} if the interaction is
     * not deferrable
     *
     * @return Non-null {@link IDeferrableCallback}
     * @throws IllegalStateException if the interaction is not deferrable
     */
    public IReplyCallback getAsReplyCallback() {
        if (!(interaction instanceof IReplyCallback)) {
            throw new IllegalStateException("Interaction is not a reply callback");
        }

        return (IReplyCallback) interaction;
    }

    /**
     * Casts interaction to {@link IMessageEditCallback}.<br> Throws {@link IllegalStateException} if the interaction is
     * not deferrable
     *
     * @return Non-null {@link IMessageEditCallback}
     * @throws IllegalStateException if the interaction is not deferrable
     */
    public IMessageEditCallback getAsMessageEditCallback() {
        if (!(interaction instanceof IMessageEditCallback)) {
            throw new IllegalStateException("Interaction is not a message edit callback");
        }

        return (IMessageEditCallback) interaction;
    }

    /**
     * Casts interaction to {@link IModalCallback}.<br> Throws {@link IllegalStateException} if the interaction is not
     * deferrable
     *
     * @return Non-null {@link IModalCallback}
     * @throws IllegalStateException if the interaction is not deferrable
     */
    public IModalCallback getAsModalCallback() {
        if (!(interaction instanceof IModalCallback)) {
            throw new IllegalStateException("Interaction is not a modal callback");
        }

        return (IModalCallback) interaction;
    }

    /**
     * Casts interaction to {@link ButtonInteractionEvent}.<br> Throws {@link IllegalStateException} if the interaction
     * is not of type {@link InteractionType#BUTTON_CLICK}
     *
     * @return Non-null {@link ButtonInteractionEvent}
     * @throws IllegalStateException if the interaction is not of type {@link InteractionType#BUTTON_CLICK}
     */
    public ButtonInteractionEvent getButtonInteractionEvent() {
        if (!(interaction instanceof ButtonInteractionEvent)) {
            throw new IllegalStateException("Interaction is not a button interaction");
        }

        return (ButtonInteractionEvent) interaction;
    }

    /**
     * Casts interaction to {@link StringSelectInteractionEvent}.<br> Throws {@link IllegalStateException} if the
     * interaction is not of type {@link InteractionType#STRING_SELECT_MENU}
     *
     * @return Non-null {@link StringSelectInteractionEvent}
     * @throws IllegalStateException if the interaction is not of type {@link InteractionType#STRING_SELECT_MENU}
     */
    public StringSelectInteractionEvent getStringSelectInteractionEvent() {
        if (!(interaction instanceof StringSelectInteractionEvent)) {
            throw new IllegalStateException("Interaction is not a string select interaction");
        }

        return (StringSelectInteractionEvent) interaction;
    }

    /**
     * Casts interaction to {@link EntitySelectInteractionEvent}.<br> Throws {@link IllegalStateException} if the
     * interaction is not of type {@link InteractionType#ENTITY_SELECT_MENU}
     *
     * @return Non-null {@link EntitySelectInteractionEvent}
     * @throws IllegalStateException if the interaction is not of type {@link InteractionType#ENTITY_SELECT_MENU}
     */
    public EntitySelectInteractionEvent getEntitySelectInteractionEvent() {
        if (!(interaction instanceof EntitySelectInteractionEvent)) {
            throw new IllegalStateException("Interaction is not an entity select interaction");
        }

        return (EntitySelectInteractionEvent) interaction;
    }

    /**
     * Casts interaction to {@link ModalInteractionEvent}.<br> Throws {@link IllegalStateException} if the interaction
     * is not of type {@link InteractionType#MODAL_SUBMITTED}
     *
     * @return Non-null {@link ModalInteractionEvent}
     * @throws IllegalStateException if the interaction is not of type {@link InteractionType#MODAL_SUBMITTED}
     */
    public ModalInteractionEvent getModalInteractionEvent() {
        if (!(interaction instanceof ModalInteractionEvent)) {
            throw new IllegalStateException("Interaction is not a modal interaction");
        }

        return (ModalInteractionEvent) interaction;
    }
}
