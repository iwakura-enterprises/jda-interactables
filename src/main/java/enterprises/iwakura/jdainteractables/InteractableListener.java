package enterprises.iwakura.jdainteractables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import enterprises.iwakura.jdainteractables.InteractionHandler.Result;
import enterprises.iwakura.jdainteractables.components.Interactable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * Register this class into your JDA/ShardManager to ensure everything related to interactive in this library will
 * work.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class InteractableListener extends ListenerAdapter {

    protected final static List<Interactable<?>> interactables = Collections.synchronizedList(new ArrayList<>());
    protected final static Timer expireCheckerTimer = new Timer("Interactable-Expire-Checker");
    protected Executor eventProcessor;

    /**
     * Creates new instance of {@link InteractableListener}
     *
     * @param eventProcessor Executor to process events, for example {@link Executors#newCachedThreadPool()}
     */
    public InteractableListener(Executor eventProcessor) {
        this.eventProcessor = eventProcessor;
        scheduleExpireCheckerTimer();
    }

    /**
     * Creates new instance of {@link InteractableListener} with cached thread pool as event processor
     */
    public InteractableListener() {
        this(Executors.newCachedThreadPool());
    }

    /**
     * Adds interactable to the list
     *
     * @param interactable {@link Interactable}
     */
    public static void addInteractable(Interactable<?> interactable) {
        interactables.add(interactable);
    }

    /**
     * Removes interactable from the list
     * </p>
     * <b>HEY YOU!</b> Yes, you! Why are you using this method? Usually, JDA Interactables removes the interactable
     * when it's expired or when the interaction is processed with {@link Result#REMOVE}.
     * <br>
     * If you are using this method, please make sure you know what you are doing, as the call could lead to
     * {@link ConcurrentModificationException}.
     * <p>This is left here for advanced use cases.</p>
     *
     * @param interactable {@link Interactable}
     * @deprecated Use with caution. See method description for details.
     */
    @Deprecated
    public static void removeInteractable(Interactable<?> interactable) {
        interactables.remove(interactable);
    }

    /**
     * Gets unmodifiable list of all interactables
     *
     * @return Unmodifiable list of all interactables
     */
    public static List<Interactable<?>> getInteractables() {
        return Collections.unmodifiableList(interactables);
    }

    /**
     * Registers timer to check for expired interactables every second
     */
    protected void scheduleExpireCheckerTimer() {
        expireCheckerTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                interactables.removeIf(interactable -> {
                    boolean expired = interactable.isExpired();
                    if (expired) {
                        interactable.runExpiryCallbacks();
                    }
                    return expired;
                });
            }
        }, 0, 1000);
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        if (ensureValidUser(event.getUser())) {
            processEvent(new InteractionEventContext(event));
        }
    }

    @Override
    public void onStringSelectInteraction(StringSelectInteractionEvent event) {
        if (ensureValidUser(event.getUser())) {
            processEvent(new InteractionEventContext(event));
        }
    }

    @Override
    public void onEntitySelectInteraction(EntitySelectInteractionEvent event) {
        if (ensureValidUser(event.getUser())) {
            processEvent(new InteractionEventContext(event));
        }
    }

    @Override
    public void onModalInteraction(ModalInteractionEvent event) {
        if (ensureValidUser(event.getUser())) {
            processEvent(new InteractionEventContext(event));
        }
    }

    /**
     * Processes the interaction event
     *
     * @param ctx The interaction event to process
     */
    protected void processEvent(InteractionEventContext ctx) {
        eventProcessor.execute(() -> {
            synchronized (interactables) {
                Iterator<Interactable<?>> iterator = interactables.iterator();
                while (iterator.hasNext()) {
                    Interactable<?> interactable = iterator.next();
                    Result result = interactable.process(ctx);
                    if (result != Result.NOT_PROCESSED) {
                        if (result == Result.REMOVE) {
                            iterator.remove();
                        }
                        // Exit early since the event has been processed
                        break;
                    }
                }
            }
        });
    }

    /**
     * Ensures the user is valid (not null and not a bot)
     *
     * @param user The user to check
     * @return true if valid, false otherwise
     */
    protected boolean ensureValidUser(User user) {
        return user != null && !user.isBot();
    }
}
