package enterprises.iwakura.jdainteractables;

import org.jetbrains.annotations.Nullable;

import lombok.AllArgsConstructor;
import lombok.Data;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/**
 * Context of the user who interacted with the interactable component. Includes the user, member (if in guild), and guild (if in guild).
 */
@Data
@AllArgsConstructor
public class UserContext {

    private final User user;
    private final @Nullable Member member;
    private final @Nullable Guild guild;

    /**
     * Creates UserContext from InteractionEventContext
     *
     * @param ctx InteractionEventContext
     * @return UserContext
     */
    public static UserContext createFrom(InteractionEventContext ctx) {
        return new UserContext(ctx.getUser(), ctx.getMember(), ctx.getGuild());
    }
}
