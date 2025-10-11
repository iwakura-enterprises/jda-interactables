package enterprises.iwakura.jdainteractables;

import java.util.UUID;

import lombok.NonNull;
import net.dv8tion.jda.api.components.buttons.Button;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;

/**
 * Interaction that can be added to interactive messages
 *
 * @param <T> Type of the component, e.g. {@link Button}, {@link SelectOption}, {@link StringSelectMenu}, or
 *            {@link EntitySelectMenu}
 * @param <E> Type of the interaction event, e.g. {@link ButtonInteractionEvent}, {@link StringSelectInteractionEvent},
 *            or {@link EntitySelectInteractionEvent}
 */
public final class Interaction<T, E> {

    private Button button;
    private SelectOption selectOption;
    private StringSelectMenu stringSelectMenu;
    private EntitySelectMenu entitySelectMenu;

    private Interaction(Button button) {
        this.button = button;
    }

    private Interaction(SelectOption selectOption) {
        this.selectOption = selectOption;
    }

    private Interaction(StringSelectMenu stringSelectMenu) {
        this.stringSelectMenu = stringSelectMenu;
    }

    private Interaction(EntitySelectMenu entitySelectMenu) {
        this.entitySelectMenu = entitySelectMenu;
    }

    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param button Button, see JDA's wiki for how to construct Button
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull Button button) {
        return new Interaction<>(button.withCustomId(UUID.randomUUID().toString()));
    }

    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param buttonStyle Button Style
     * @param label       Button Label
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull ButtonStyle buttonStyle, String label) {
        return new Interaction<>(Button.of(buttonStyle, UUID.randomUUID().toString(), label));
    }

    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param buttonStyle Button Style
     * @param label       Button Label
     * @param disabled    {@link Button#isDisabled()}
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull ButtonStyle buttonStyle, String label,
        boolean disabled) {
        return new Interaction<>(Button.of(buttonStyle, UUID.randomUUID().toString(), label).withDisabled(disabled));
    }


    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param buttonStyle Button Style
     * @param label       Button Label
     * @param emoji       Button Emoji
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull ButtonStyle buttonStyle, String label,
        Emoji emoji) {
        return new Interaction<>(Button.of(buttonStyle, UUID.randomUUID().toString(), label, emoji));
    }

    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param buttonStyle Button Style
     * @param label       Button Label
     * @param emoji       Button Emoji
     * @param disabled    {@link Button#isDisabled()}
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull ButtonStyle buttonStyle, String label,
        Emoji emoji,
        boolean disabled) {
        return new Interaction<>(
            Button.of(buttonStyle, UUID.randomUUID().toString(), label, emoji).withDisabled(disabled));
    }

    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param buttonStyle Button Style
     * @param emoji       Button Emoji
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull ButtonStyle buttonStyle, Emoji emoji) {
        return new Interaction<>(Button.of(buttonStyle, UUID.randomUUID().toString(), emoji));
    }

    /**
     * Creates {@link Interaction} with Button, the ID will be randomized
     *
     * @param buttonStyle Button Style
     * @param emoji       Button Emoji
     * @param disabled    {@link Button#isDisabled()}
     * @return {@link Interaction} object
     */
    public static Interaction<Button, ButtonInteractionEvent> asButton(@NonNull ButtonStyle buttonStyle, Emoji emoji,
        boolean disabled) {
        return new Interaction<>(Button.of(buttonStyle, UUID.randomUUID().toString(), emoji).withDisabled(disabled));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param selectOption Select Option, see JDA's wiki for how to construct Select Option
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(
        @NonNull SelectOption selectOption) {
        return new Interaction<>(selectOption.withValue(UUID.randomUUID().toString()));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label Select Option Label
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label) {
        return new Interaction<>(SelectOption.of(label, UUID.randomUUID().toString()));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label     Select Option Label
     * @param isDefault {@link SelectOption}'s {@link SelectOption#isDefault()}
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        boolean isDefault) {
        return new Interaction<>(SelectOption.of(label, UUID.randomUUID().toString()).withDefault(isDefault));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label     Select Option Label
     * @param emoji     Select Option Emoji
     * @param isDefault {@link SelectOption}'s {@link SelectOption#isDefault()}
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        boolean isDefault,
        @NonNull Emoji emoji) {
        return new Interaction<>(
            SelectOption.of(label, UUID.randomUUID().toString()).withDefault(isDefault).withEmoji(emoji));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label Select Option Label
     * @param emoji Select Option Emoji
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        @NonNull Emoji emoji) {
        return new Interaction<>(SelectOption.of(label, UUID.randomUUID().toString()).withEmoji(emoji));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label       Select Option Label
     * @param description Select Option description
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        @NonNull String description) {
        return new Interaction<>(SelectOption.of(label, UUID.randomUUID().toString()).withDescription(description));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label       Select Option Label
     * @param description Select Option description
     * @param isDefault   {@link SelectOption}'s {@link SelectOption#isDefault()}
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        @NonNull String description,
        boolean isDefault) {
        return new Interaction<>(
            SelectOption.of(label, UUID.randomUUID().toString()).withDescription(description).withDefault(isDefault));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label       Select Option Label
     * @param description Select Option description
     * @param isDefault   {@link SelectOption}'s {@link SelectOption#isDefault()}
     * @param emoji       Select Option Emoji
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        @NonNull String description,
        boolean isDefault,
        @NonNull Emoji emoji) {
        return new Interaction<>(SelectOption.of(label, UUID.randomUUID().toString())
            .withDescription(description)
            .withDefault(isDefault)
            .withEmoji(emoji));
    }

    /**
     * Creates {@link Interaction} with Select Option, the value will be randomized
     *
     * @param label       Select Option Label
     * @param description Select Option description
     * @param emoji       Select Option Emoji
     * @return {@link Interaction} object
     */
    public static Interaction<SelectOption, StringSelectInteractionEvent> asSelectOption(@NonNull String label,
        @NonNull String description,
        @NonNull Emoji emoji) {
        return new Interaction<>(
            SelectOption.of(label, UUID.randomUUID().toString()).withDescription(description).withEmoji(emoji));
    }

    /**
     * Creates {@link Interaction} with String Select Menu, the ID will be randomized
     *
     * @param builder String Select Menu Builder, see JDA's wiki for how to construct String Select Menu
     * @return {@link Interaction} object
     */
    public static Interaction<StringSelectMenu, StringSelectInteractionEvent> asStringSelectMenu(
        @NonNull StringSelectMenu.Builder builder) {
        return new Interaction<>(builder.setCustomId(UUID.randomUUID().toString()).build());
    }

    /**
     * Creates {@link Interaction} with String Select Menu, the ID will be randomized
     *
     * @param placeholder Placeholder text when nothing is selected
     * @param minValues   Minimum number of selections
     * @param maxValues   Maximum number of selections
     * @param options     Select Options
     * @return {@link Interaction} object
     */
    public static Interaction<StringSelectMenu, StringSelectInteractionEvent> asStringSelectMenu(
        @NonNull String placeholder,
        int minValues,
        int maxValues,
        @NonNull SelectOption... options
    ) {
        return new Interaction<>(StringSelectMenu.create(UUID.randomUUID().toString())
            .setPlaceholder(placeholder)
            .setMinValues(minValues)
            .setMaxValues(maxValues)
            .addOptions(options)
            .build());
    }

    /**
     * Creates {@link Interaction} with Entity Select Menu, the ID will be randomized
     *
     * @param builder Entity Select Menu Builder, see JDA's wiki for how to construct Entity Select Menu
     * @return {@link Interaction} object
     */
    public static Interaction<EntitySelectMenu, EntitySelectInteractionEvent> asEntitySelectMenu(
        @NonNull EntitySelectMenu.Builder builder) {
        return new Interaction<>(builder.setCustomId(UUID.randomUUID().toString()).build());
    }

    /**
     * Creates {@link Interaction} with Entity Select Menu, the ID will be randomized
     *
     * @param placeholder Placeholder text when nothing is selected
     * @param type        The type of entities that can be selected
     * @param types       Additional types of entities that can be selected
     * @return {@link Interaction} object
     */
    public static Interaction<EntitySelectMenu, EntitySelectInteractionEvent> asEntitySelectMenu(
        @NonNull String placeholder,
        @NonNull SelectTarget type,
        @NonNull SelectTarget... types
    ) {
        return new Interaction<>(EntitySelectMenu.create(UUID.randomUUID().toString(), type, types)
            .setPlaceholder(placeholder)
            .build());
    }

    /**
     * Creates {@link Interaction} with Entity Select Menu, the ID will be randomized
     *
     * @param placeholder Placeholder text when nothing is selected
     * @param minValues   Minimum number of selections
     * @param maxValues   Maximum number of selections
     * @param type        The type of entities that can be selected
     * @param types       Additional types of entities that can be selected
     * @return {@link Interaction} object
     */
    public static Interaction<EntitySelectMenu, EntitySelectInteractionEvent> asEntitySelectMenu(
        @NonNull String placeholder,
        int minValues,
        int maxValues,
        @NonNull SelectTarget type,
        @NonNull SelectTarget... types
    ) {
        return new Interaction<>(EntitySelectMenu.create(UUID.randomUUID().toString(), type, types)
            .setPlaceholder(placeholder)
            .setMinValues(minValues)
            .setMaxValues(maxValues)
            .build());
    }

    /**
     * Determines if {@link Interaction} is Button
     *
     * @return true if {@link Interaction} is Button
     */
    public boolean isButton() {
        return button != null;
    }

    /**
     * Determines if {@link Interaction} is Select Option
     *
     * @return true if {@link Interaction} is Select Option
     */
    public boolean isSelectOption() {
        return selectOption != null;
    }

    /**
     * Determines if {@link Interaction} is String Select Menu
     *
     * @return true if {@link Interaction} is String Select Menu
     */
    public boolean isStringSelectMenu() {
        return stringSelectMenu != null;
    }

    /**
     * Determines if {@link Interaction} is Entity Select Menu
     *
     * @return true if {@link Interaction} is Entity Select Menu
     */
    public boolean isEntitySelectMenu() {
        return entitySelectMenu != null;
    }

    /**
     * Gets the underlying component, which can be {@link Button}, {@link SelectOption}, or {@link EntitySelectMenu}
     *
     * @return Non-null underlying component
     * @throws IllegalStateException if {@link Interaction} is neither Button nor Select Option nor Entity Select Menu
     */
    public T getComponent() {
        if (isButton()) {
            return (T) button;
        }

        if (isSelectOption()) {
            return (T) selectOption;
        }

        if (isStringSelectMenu()) {
            return (T) stringSelectMenu;
        }

        if (isEntitySelectMenu()) {
            return (T) entitySelectMenu;
        }

        throw new IllegalStateException("Interaction is neither Button nor Select Option nor Entity Select Menu");
    }

    /**
     * Gets Button
     *
     * @return Non-null {@link Button}
     * @throws IllegalStateException if {@link Interaction} is not Button
     */
    public Button getButton() {
        if (isButton()) {
            return button;
        }

        throw new IllegalStateException("Interaction is not Button");
    }

    /**
     * Gets Select Option
     *
     * @return Non-null {@link SelectOption}
     * @throws IllegalStateException if {@link Interaction} is not Select Option
     */
    public SelectOption getSelectOption() {
        if (isSelectOption()) {
            return selectOption;
        }

        throw new IllegalStateException("Interaction is not Select Option");
    }

    /**
     * Gets String Select Menu
     *
     * @return Non-null {@link StringSelectMenu}
     * @throws IllegalStateException if {@link Interaction} is not String Select Menu
     */
    public StringSelectMenu getStringSelectMenu() {
        if (isStringSelectMenu()) {
            return stringSelectMenu;
        }

        throw new IllegalStateException("Interaction is not String Select Menu");
    }

    /**
     * Gets Entity Select Menu
     *
     * @return Non-null {@link EntitySelectMenu}
     * @throws IllegalStateException if {@link Interaction} is not Entity Select Menu
     */
    public EntitySelectMenu getEntitySelectMenu() {
        if (isEntitySelectMenu()) {
            return entitySelectMenu;
        }

        throw new IllegalStateException("Interaction is not Entity Select Menu");
    }

    /**
     * Gets {@link Interaction}'s type
     *
     * @return Non-null {@link InteractionType}
     */
    public InteractionType getType() {
        if (isButton()) {
            return InteractionType.BUTTON_CLICK;
        }

        if (isSelectOption()) {
            return InteractionType.STRING_SELECT_MENU;
        }

        if (isEntitySelectMenu()) {
            return InteractionType.ENTITY_SELECT_MENU;
        }

        if (isStringSelectMenu()) {
            return InteractionType.STRING_SELECT_MENU;
        }

        throw new IllegalStateException("Interaction is neither Button nor Select Option nor Entity Select Menu");
    }
}
