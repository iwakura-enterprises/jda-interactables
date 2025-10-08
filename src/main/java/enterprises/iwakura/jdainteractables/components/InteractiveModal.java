package enterprises.iwakura.jdainteractables.components;

import java.util.UUID;
import java.util.function.Consumer;

import enterprises.iwakura.jdainteractables.GroupedInteractionEvent;
import enterprises.iwakura.jdainteractables.Interaction;
import enterprises.iwakura.jdainteractables.InteractableListener;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.callbacks.IModalCallback;
import net.dv8tion.jda.api.interactions.commands.SlashCommandInteraction;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.requests.restaction.interactions.ModalCallbackAction;

/**
 * Interactive modal window, can be used to reply to {@link IModalCallback} such as {@link SlashCommandInteraction} or {@link ButtonInteractionEvent}
 */
public class InteractiveModal extends Interactable {

    // Interaction
    protected final @Getter Consumer<ModalInteractionEvent> modalClosedConsumer;

    // Settings
    protected final @Getter Modal.Builder modalBuilder;

    protected InteractiveModal(String modalTitle, Consumer<Modal.Builder> modalBuilder, Consumer<ModalInteractionEvent> modalClosedConsumer) {
        this.modalBuilder = Modal.create(UUID.randomUUID().toString(), modalTitle);
        modalBuilder.accept(this.modalBuilder);
        this.modalClosedConsumer = modalClosedConsumer;
    }

    /**
     * Creates new {@link InteractiveModal} with specified title and {@link Consumer} with {@link ModalInteractionEvent} which is called when the
     * modal window is closed
     *
     * @param title         Non-null title
     * @param modalBuilder  Non-null {@link Consumer} with {@link Modal.Builder}
     * @param onModalClosed Non-null {@link Consumer} with {@link ModalInteractionEvent}
     * @return Non-null {@link InteractiveModal}
     */
    public static @NonNull InteractiveModal createTitled(@NonNull String title, @NonNull Consumer<Modal.Builder> modalBuilder,
        @NonNull Consumer<ModalInteractionEvent> onModalClosed) {
        return new InteractiveModal(title, modalBuilder, onModalClosed);
    }

    /**
     * Replies to {@link IModalCallback} with this interactive modal. {@link IModalCallback} is for example {@link SlashCommandInteraction} or
     * {@link ButtonInteractionEvent}
     *
     * @param modalCallback Non-null {@link IModalCallback}
     * @return Non-null {@link ModalCallbackAction}
     */
    public ModalCallbackAction replyModal(@NonNull IModalCallback modalCallback) {
        modalBuilder.setId(UUID.randomUUID().toString());
        InteractableListener.addInteractable(this);
        return modalCallback.replyModal(modalBuilder.build());
    }

    ///////////////
    // Overrides //

    /// ////////////

    @Override
    public void process(GroupedInteractionEvent event) {
        if (isApplicable(null, event)) {
            modalClosedConsumer.accept(event.getModalInteractionEvent());
            InteractableListener.removeInteractable(this);
        }
    }

    @Override
    public boolean isApplicable(Interaction interaction, GroupedInteractionEvent event) {
        return event.isModalInteraction() && modalBuilder.getId().equals(event.getModalInteractionEvent().getModalId());
    }

    @Override
    public boolean canUserInteract(@NonNull User user) {
        return true;
    }
}
