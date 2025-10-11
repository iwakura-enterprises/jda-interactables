package enterprises.iwakura.jdainteractables.components;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import enterprises.iwakura.jdainteractables.InteractableListener;
import enterprises.iwakura.jdainteractables.Interaction;
import enterprises.iwakura.jdainteractables.InteractionDeniedCallback;
import enterprises.iwakura.jdainteractables.InteractionEventContext;
import enterprises.iwakura.jdainteractables.InteractionHandler;
import enterprises.iwakura.jdainteractables.InteractionHandler.Result;
import enterprises.iwakura.jdainteractables.InteractionRule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents something that can be interacted with, like messages, modals, etc.
 *
 * @param <T> The type of the interactable itself, useful for method chaining
 */
@Slf4j
@Getter
@Setter
public abstract class Interactable<T extends Interactable<?>> {

    protected final UUID id = UUID.randomUUID();
    protected final long createdAtMillis = System.currentTimeMillis();
    protected final List<InteractionRule> interactionRuleList = Collections.synchronizedList(new ArrayList<>());
    protected final List<Runnable> expiryCallbacks = Collections.synchronizedList(new ArrayList<>());
    protected final List<InteractionDeniedCallback> interactionDeniedCallbacks = Collections.synchronizedList(
        new ArrayList<>());

    protected Duration expiryDuration = Duration.ofMinutes(5);

    /**
     * Processes the interaction event
     *
     * @param ctx The interaction event context
     * @return The result of the interaction processing
     */
    public InteractionHandler.Result process(InteractionEventContext ctx) {
        if (!canInteract(ctx)) {
            runInteractionDeniedCallbacks(ctx);
            return Result.IGNORE;
        }
        return InteractionHandler.Result.NOT_PROCESSED;
    }

    /**
     * Registers this interactable with the {@link InteractableListener}
     *
     * @return this interactable
     */
    public T registerNow() {
        InteractableListener.addInteractable(this);
        return (T) this;
    }

    /**
     * Returns a consumer that registers this interactable in the {@link InteractableListener} after the rest action is
     * completed.
     *
     * @return A consumer that registers this interactable
     */
    public Consumer<? super Object> registerOnCompleted() {
        return obj -> this.registerNow();
    }

    /**
     * Checks if the event is applicable to this interactable
     *
     * @param interaction The interaction to check
     * @param ctx         The event context to check
     * @return true if applicable, false otherwise
     */
    public boolean isApplicable(Interaction<?, ?> interaction, InteractionEventContext ctx) {
        return interaction.getType() == ctx.getInteractionType() && canInteract(ctx);
    }

    /**
     * Adds an interaction rule to the interactable
     *
     * @param interactionRule The interaction rule to add
     * @return The interactable itself for chaining
     */
    public T addInteractionRule(InteractionRule interactionRule) {
        interactionRuleList.add(interactionRule);
        return (T) this;
    }

    /**
     * Checks if the user can interact with this interactable. If no interaction firewall functions are set, everyone
     * can interact. If any function returns true, the user can interact. If no interaction rule returns allow or deny
     * (e.g., all neutral), the user cannot interact.
     * <p>
     * If an interaction rule throws an exception, the error is logged and the user is disallowed from interacting.
     *
     * @param ctx Interaction event context
     * @return true if the user can interact, false otherwise
     */
    public boolean canInteract(InteractionEventContext ctx) {
        if (interactionRuleList.isEmpty()) {
            return true;
        }

        synchronized (interactionRuleList) {
            for (InteractionRule interactionRule : interactionRuleList) {
                try {
                    InteractionRule.Result result = interactionRule.apply(ctx);
                    if (result == InteractionRule.Result.ALLOW) {
                        return true;
                    } else if (result == InteractionRule.Result.DENY) {
                        return false;
                    }
                } catch (Exception exception) {
                    log.error("Error while applying interaction rule for interactable {}, disallowing interaction",
                        id, exception);
                    return false;
                }
            }
        }

        return false;
    }

    /**
     * Adds a callback when the interactable expires
     *
     * @param runnable The callback to run
     */
    public void addExpiryCallback(Runnable runnable) {
        expiryCallbacks.add(runnable);
    }

    /**
     * Clears all expire callbacks
     */
    public void clearExpiryCallbacks() {
        expiryCallbacks.clear();
    }

    /**
     * Called when the interactable expires
     */
    public void runExpiryCallbacks() {
        synchronized (expiryCallbacks) {
            for (Runnable runnable : expiryCallbacks) {
                try {
                    runnable.run();
                } catch (Exception exception) {
                    log.error("Error while running onExpire runnable for interactable {}", id, exception);
                }
            }
        }
    }

    /**
     * Adds a callback when an interaction is denied
     *
     * @param callback The callback to run
     */
    public void addInteractionDeniedCallback(InteractionDeniedCallback callback) {
        interactionDeniedCallbacks.add(callback);
    }

    /**
     * Clears all interaction denied callbacks
     */
    public void clearInteractionDeniedCallbacks() {
        interactionDeniedCallbacks.clear();
    }

    /**
     * Called when an interaction is denied
     *
     * @param ctx The interaction event context
     */
    protected void runInteractionDeniedCallbacks(InteractionEventContext ctx) {
        synchronized (interactionDeniedCallbacks) {
            for (Consumer<InteractionEventContext> consumer : interactionDeniedCallbacks) {
                try {
                    consumer.accept(ctx);
                } catch (Exception exception) {
                    log.error("Error while running onInteractionDenied consumer for interactable {}", id, exception);
                }
            }
        }
    }

    /**
     * Checks if the interactable is expired
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - createdAtMillis >= expiryDuration.toMillis();
    }

    /**
     * Clears the interaction rules
     */
    public void clearInteractionRules() {
        interactionRuleList.clear();
    }
}
