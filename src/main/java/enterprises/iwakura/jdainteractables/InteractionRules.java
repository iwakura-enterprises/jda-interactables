package enterprises.iwakura.jdainteractables;

import java.util.Arrays;
import java.util.List;

import enterprises.iwakura.jdainteractables.InteractionRule.Result;
import lombok.experimental.UtilityClass;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

/**
 * Set of common interaction firewall rules
 */
@UtilityClass
public class InteractionRules {

    /**
     * Allows only specified users to interact
     *
     * @param users List of users to allow
     * @return Firewall rule
     */
    public static InteractionRule allowUsers(List<User> users) {
        return user -> {
            for (User u : users) {
                if (u.getIdLong() == user.getUser().getIdLong()) {
                    return Result.ALLOW;
                }
            }
            return Result.NEUTRAL;
        };
    }

    /**
     * Allows only specified users to interact
     *
     * @param users Array of users to allow
     * @return Firewall rule
     */
    public static InteractionRule allowUsers(User... users) {
        return allowUsers(Arrays.asList(users));
    }

    /**
     * Denies specified users from interacting
     *
     * @param users List of users to deny
     * @return Firewall rule
     */
    public static InteractionRule denyUsers(List<User> users) {
        return user -> {
            for (User u : users) {
                if (u.getIdLong() == user.getUser().getIdLong()) {
                    return Result.DENY;
                }
            }
            return Result.NEUTRAL;
        };
    }

    /**
     * Denies specified users from interacting
     *
     * @param users Array of users to deny
     * @return Firewall rule
     */
    public static InteractionRule denyUsers(User... users) {
        return denyUsers(Arrays.asList(users));
    }

    /**
     * Allows only users with specified roles to interact
     *
     * @param roles List of roles to allow
     * @return Firewall rule
     */
    public static InteractionRule allowRoles(List<Role> roles) {
        return user -> {
            if (user.getGuild() == null || user.getMember() == null) {
                return Result.NEUTRAL;
            }
            for (Role r : roles) {
                if (user.getMember().getRoles().contains(r)) {
                    return Result.ALLOW;
                }
            }
            return Result.NEUTRAL;
        };
    }

    /**
     * Allows only users with specified roles to interact
     *
     * @param roles Array of roles to allow
     * @return Firewall rule
     */
    public static InteractionRule allowRoles(Role... roles) {
        return allowRoles(Arrays.asList(roles));
    }

    /**
     * Denies users with specified roles from interacting
     *
     * @param roles List of roles to deny
     * @return Firewall rule
     */
    public static InteractionRule denyRoles(List<Role> roles) {
        return user -> {
            if (user.getGuild() == null || user.getMember() == null) {
                return Result.NEUTRAL;
            }
            for (Role r : roles) {
                if (user.getMember().getRoles().contains(r)) {
                    return Result.DENY;
                }
            }
            return Result.ALLOW;
        };
    }

    /**
     * Denies users with specified roles from interacting
     *
     * @param roles Array of roles to deny
     * @return Firewall rule
     */
    public static InteractionRule denyRoles(Role... roles) {
        return denyRoles(Arrays.asList(roles));
    }
}
