package enterprises.iwakura.jdainteractables.components;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import enterprises.iwakura.jdainteractables.GroupedInteractionEvent;
import enterprises.iwakura.jdainteractables.Interaction;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.entities.User;

/**
 * Represents something that can be interacted with, like messages, modals, etc.
 */
@Slf4j
@Getter
@Setter
public abstract class Interactable {

    protected final UUID id = UUID.randomUUID();
    protected final long createdAtMillis = System.currentTimeMillis();
    protected final List<Long> whitelistedUsers = new ArrayList<>();
    protected final List<Runnable> expireCallbacks = new ArrayList<>();

    protected Duration expireDuration = Duration.ofMinutes(5);

    /**
     * Processes the interaction event
     *
     * @param interactionEvent The interaction event to process
     */
    public abstract void process(GroupedInteractionEvent interactionEvent);

    /**
     * Checks if the event is applicable to this interactable
     *
     * @param interaction The interaction to check
     * @param event       The event to check
     * @return true if applicable, false otherwise
     */
    public abstract boolean isApplicable(Interaction interaction, GroupedInteractionEvent event);

    /**
     * Determines if the user can interact with this interactable
     *
     * @param user The user to check
     * @return true if the user can interact, false otherwise
     */
    public boolean canUserInteract(User user) {
        return whitelistedUsers.isEmpty() || whitelistedUsers.contains(user.getIdLong());
    }

    /**
     * Adds a callback when the interactable expires
     *
     * @param runnable The callback to run
     */
    public void addOnExpireCallback(Runnable runnable) {
        expireCallbacks.add(runnable);
    }

    /**
     * Clears all expire callbacks
     */
    public void clearOnExpireCallback() {
        expireCallbacks.clear();
    }

    /**
     * Called when the interactable expires
     */
    public void runOnExpireCallbacks() {
        for (Runnable runnable : expireCallbacks) {
            try {
                runnable.run();
            } catch (Exception exception) {
                log.error("Error while running onExpire runnable for interactable {}", id, exception);
            }
        }
    }

    /**
     * Checks if the interactable is expired
     *
     * @return true if expired, false otherwise
     */
    public boolean isExpired() {
        return System.currentTimeMillis() - createdAtMillis >= expireDuration.toMillis();
    }
}
