package enterprises.iwakura.jdainteractables;

import java.util.function.Consumer;

import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback;

/**
 * Set of common interaction denied callbacks.
 */
@UtilityClass
public class InteractionDeniedCallbacks {

    /**
     * Defers the reply and accepts a callback to customize the reply.
     *
     * @param callback The callback to customize the reply
     * @return The interaction denied callback
     */
    public static InteractionDeniedCallback deferReply(Consumer<IReplyCallback> callback) {
        return ctx -> callback.accept(ctx.getAsReplyCallback());
    }

    /**
     * Defers the reply with specified content and ephemeral set to true.
     *
     * @return The interaction denied callback
     */
    public static InteractionDeniedCallback deferReplyEphemeral(String content) {
        return deferReply(r -> r.deferReply(true).setContent(content).queue());
    }

    /**
     * Defers the reply with specified embed and ephemeral set to true.
     *
     * @return The interaction denied callback
     */
    public static InteractionDeniedCallback deferReplyEphemeral(MessageEmbed... embeds) {
        return deferReply(r -> r.deferReply(true).setEmbeds(embeds).queue());
    }
}
