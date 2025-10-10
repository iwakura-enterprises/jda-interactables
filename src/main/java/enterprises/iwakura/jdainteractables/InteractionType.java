package enterprises.iwakura.jdainteractables;

/**
 * Type of interaction that was performed by the user.
 */
public enum InteractionType {
    /**
     * User clicked on a button.
     */
    BUTTON_CLICK,

    /**
     * User confirmed a string select menu with one or more options.
     */
    STRING_SELECT_MENU,

    /**
     * User confirmed a user select menu with one or more options.
     */
    ENTITY_SELECT_MENU,

    /**
     * User confirmed a modal (form) submission.
     */
    MODAL_SUBMITTED;
}
