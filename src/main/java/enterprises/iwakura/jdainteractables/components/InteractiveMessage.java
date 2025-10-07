package enterprises.iwakura.jdainteractables.components;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import enterprises.iwakura.jdainteractables.GroupedInteractionEvent;
import enterprises.iwakura.jdainteractables.Interaction;
import enterprises.iwakura.jdainteractables.InteractionType;
import enterprises.iwakura.jdainteractables.InteractiveListener;
import enterprises.iwakura.jdainteractables.exceptions.CannotAddInteractionException;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.channel.unions.MessageChannelUnion;
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectMenu;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.requests.restaction.MessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.MessageEditAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageCreateAction;
import net.dv8tion.jda.api.requests.restaction.WebhookMessageEditAction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.api.utils.messages.MessageEditData;

@Getter
public class InteractiveMessage extends MessageInteractable {

    // Interactions
    protected final Map<Interaction, Consumer<GroupedInteractionEvent>> interactions = new LinkedHashMap<>();

    // Other
    protected MessageEditBuilder messageEditBuilder;
    protected SelectMenu.Builder<?, ?> selectMenuBuilder;
    protected Consumer<StringSelectInteractionEvent> stringSelectInteractionEventConsumer = event -> {
    };
    protected Consumer<EntitySelectInteractionEvent> entitySelectInteractionEventConsumer = event -> {
    };

    //region Constructors

    protected InteractiveMessage() {
        messageEditBuilder = new MessageEditBuilder();
    }

    protected InteractiveMessage(MessageEditBuilder messageEditBuilder) {
        this.messageEditBuilder = messageEditBuilder;
    }

    protected InteractiveMessage(MessageEditBuilder messageEditBuilder, SelectMenu.Builder<?, ?> selectMenuBuilder) {
        this.messageEditBuilder = messageEditBuilder;
        this.selectMenuBuilder = selectMenuBuilder;
        this.selectMenuBuilder.setId(id.toString());
    }

    /**
     * Creates an empty {@link InteractiveMessage} object ({@link MessageEditBuilder} is empty)
     *
     * @return {@link InteractiveMessage}
     */
    public static InteractiveMessage createEmpty() {
        return new InteractiveMessage();
    }

    /**
     * Creates {@link InteractiveMessage} object with {@link MessageEditBuilder}
     *
     * @param messageEditBuilder Non-null {@link MessageEditBuilder}
     * @return {@link InteractiveMessage}
     */
    public static InteractiveMessage create(@NonNull MessageEditBuilder messageEditBuilder) {
        return new InteractiveMessage(messageEditBuilder);
    }

    /**
     * Creates {@link InteractiveMessage} object with {@link MessageEditBuilder} and {@link SelectMenu.Builder}
     *
     * @param messageEditBuilder Non-null {@link MessageEditBuilder}
     * @param selectMenuBuilder  Non-null {@link SelectMenu.Builder}
     * @return {@link InteractiveMessage}
     */
    public static InteractiveMessage create(@NonNull MessageEditBuilder messageEditBuilder, @NonNull SelectMenu.Builder selectMenuBuilder) {
        return new InteractiveMessage(messageEditBuilder, selectMenuBuilder);
    }

    /**
     * Creates {@link InteractiveMessage} object with {@link MessageEditBuilder} and specified {@link SelectMenu.Builder}. <strong>The ID of select
     * menu will be randomized.</strong>
     *
     * @param messageEditBuilder Non-null {@link MessageEditBuilder}
     * @param selectMenuBuilder  Non-null {@link SelectMenu.Builder}
     * @return {@link InteractiveMessage}
     */
    public static InteractiveMessage createSelectMenu(@NonNull MessageEditBuilder messageEditBuilder,
        @NonNull SelectMenu.Builder<?, ?> selectMenuBuilder) {
        return new InteractiveMessage(messageEditBuilder, selectMenuBuilder);
    }

    /**
     * Creates {@link InteractiveMessage} object with {@link MessageEditBuilder} and randomly created {@link StringSelectMenu.Builder} with specified
     * placeholder
     *
     * @param messageEditBuilder    Non-null {@link MessageEditBuilder}
     * @param selectMenuPlaceholder Non-null Select menu placeholder
     * @return {@link InteractiveMessage}
     */
    public static InteractiveMessage createStringSelectMenu(@NonNull MessageEditBuilder messageEditBuilder, @NonNull String selectMenuPlaceholder) {
        StringSelectMenu.Builder selectMenuBuilder = StringSelectMenu.create(UUID.randomUUID().toString()); // The ID will be set in the constructor
        selectMenuBuilder.setPlaceholder(selectMenuPlaceholder);
        return new InteractiveMessage(messageEditBuilder, selectMenuBuilder);
    }

    /**
     * Creates {@link InteractiveMessage} object with {@link MessageEditBuilder} and randomly created {@link EntitySelectMenu.Builder} with specified
     * placeholder
     *
     * @param messageEditBuilder    Non-null {@link MessageEditBuilder}
     * @param selectMenuPlaceholder Non-null Select menu placeholder
     * @param selectTarget          Non-null {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget}
     * @param selectTargets         {@link net.dv8tion.jda.api.interactions.components.selections.EntitySelectMenu.SelectTarget} array
     * @return {@link InteractiveMessage}
     */
    public static InteractiveMessage createEntitySelectMenu(@NonNull MessageEditBuilder messageEditBuilder, @NonNull String selectMenuPlaceholder,
        @NonNull EntitySelectMenu.SelectTarget selectTarget, EntitySelectMenu.SelectTarget... selectTargets) {
        EntitySelectMenu.Builder selectMenuBuilder;

        // The ID will be set in the constructor
        if (selectTargets != null) {
            selectMenuBuilder = EntitySelectMenu.create(UUID.randomUUID().toString(), EnumSet.of(selectTarget, selectTargets));
        } else {
            selectMenuBuilder = EntitySelectMenu.create(UUID.randomUUID().toString(), selectTarget);
        }

        selectMenuBuilder.setPlaceholder(selectMenuPlaceholder);
        return new InteractiveMessage(messageEditBuilder, selectMenuBuilder);
    }

    //endregion

    //region Interactions

    public InteractiveMessage addInteraction(Interaction interaction, Consumer<GroupedInteractionEvent> onInteracted) {
        Map<Interaction, Consumer<GroupedInteractionEvent>> interactionsButtons = getInteractionByType(InteractionType.BUTTON_CLICK);
        Map<Interaction, Consumer<GroupedInteractionEvent>> interactionsSelectOptions = getInteractionByType(
            InteractionType.STRING_SELECT_MENU_OPTION_CLICK);

        if (!interactionsButtons.isEmpty() && (interaction.getType() == InteractionType.STRING_SELECT_MENU_OPTION_CLICK)) {
            throw new CannotAddInteractionException("Cannot add Select Option interaction! Message can only have Buttons or Select Menu.",
                interaction);
        }

        if (!interactionsSelectOptions.isEmpty() && interaction.getType() == InteractionType.BUTTON_CLICK) {
            throw new CannotAddInteractionException("Cannot add Button interaction! Message can only have Buttons or Select Menu.", interaction);
        }

        if (interactionsButtons.size() == 25) {
            throw new CannotAddInteractionException("Cannot add Button interaction! Maximum number of buttons for message is 25.", interaction);
        }

        if (interactionsSelectOptions.size() == 25) {
            throw new CannotAddInteractionException("Cannot add Select Option interaction! Maximum number of select options for message is 25.",
                interaction);
        }

        if (interaction.getType() == InteractionType.ENTITY_SELECT_MENU_OPTION_CLICK) {
            throw new CannotAddInteractionException(
                "Cannot add Entity Select Option interaction! Please, use #onEntitySelectMenuInteracted() method to handle selected values!",
                interaction
            );
        }

        if (interaction.getType() == InteractionType.STRING_SELECT_MENU_OPTION_CLICK) {
            if (selectMenuBuilder instanceof EntitySelectMenu.Builder) {
                throw new CannotAddInteractionException(
                    "You cannot add interaction to entity select menu! Please, use #onEntitySelectMenuInteracted() method to handle selected values!",
                    interaction);
            }
        }

        interactions.put(interaction, onInteracted);
        return this;
    }

    /**
     * Adds interaction with empty action (does nothing when interacted)
     *
     * @param interaction Non-null {@link Interaction}
     * @return Current {@link InteractiveMessage} instance
     */
    public InteractiveMessage addInteractionEmpty(Interaction interaction) {
        return addInteraction(interaction, ignored -> {
        });
    }

    /**
     * Adds user to whitelist. If whitelist is empty, everyone can interact
     *
     * @param users Non-null {@link User} array
     * @return Current {@link InteractiveMessage} instance
     */
    public InteractiveMessage addUsersToWhitelist(User... users) {
        for (User user : users) {
            whitelistedUsers.add(user.getIdLong());
        }
        return this;
    }

    /**
     * Removes user from whitelist
     *
     * @param users Non-null {@link User} array
     * @return Current {@link InteractiveMessage} instance
     */
    public InteractiveMessage removeUsersFromWhitelist(User... users) {
        for (User user : users) {
            whitelistedUsers.remove(user.getIdLong());
        }
        return this;
    }

    /**
     * Clears the whitelist
     *
     * @return Current {@link InteractiveMessage} instance
     */
    public InteractiveMessage clearWhitelist() {
        whitelistedUsers.clear();
        return this;
    }

    /**
     * Adds action when string select menu is interacted.
     *
     * @param onInteracted Non-null {@link Consumer} of {@link StringSelectInteractionEvent}
     * @return Current {@link InteractiveMessage} instance
     */
    public InteractiveMessage onStringSelectMenuInteracted(@NonNull Consumer<StringSelectInteractionEvent> onInteracted) {
        stringSelectInteractionEventConsumer = onInteracted;
        return this;
    }

    /**
     * Adds action when entity select menu is interacted.
     *
     * @param onInteracted Non-null {@link Consumer} of {@link EntitySelectInteractionEvent}
     * @return Current {@link InteractiveMessage} instance
     */
    public InteractiveMessage onEntitySelectMenuInteracted(@NonNull Consumer<EntitySelectInteractionEvent> onInteracted) {
        entitySelectInteractionEventConsumer = onInteracted;
        return this;
    }

    //endregion

    //region sending / editing / replying

    /**
     * Sends the interactive message to the specified {@link MessageChannelUnion}
     *
     * @param messageChannelUnion Non-null {@link MessageChannelUnion}
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> sendMessage(@NonNull MessageChannelUnion messageChannelUnion) {
        return sendEx(messageChannelUnion, null, false, false, null, null);
    }

    /**
     * Edits specified {@link Message} with the interactive message
     *
     * @param message Non-null {@link Message}
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> editMessage(@NonNull Message message) {
        return sendEx(null, null, false, false, message, null);
    }

    /**
     * Replies to the specified {@link Message}
     *
     * @param message Non-null {@link Message}
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> replyTo(@NonNull Message message) {
        return sendEx(null, null, false, false, null, message);
    }

    /**
     * Sends the interactive message to the specified {@link InteractionHook}
     *
     * @param interactionHook Non-null {@link InteractionHook}
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> sendMessage(@NonNull InteractionHook interactionHook) {
        return sendEx(null, interactionHook, false, false, null, null);
    }

    /**
     * Sends the interactive message to the specified {@link InteractionHook} with specified ephemeral
     *
     * @param interactionHook Non-null {@link InteractionHook}
     * @param ephemeral       Ephemeral
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> sendMessage(@NonNull InteractionHook interactionHook, boolean ephemeral) {
        return sendEx(null, interactionHook.setEphemeral(ephemeral), ephemeral, false, null, null);
    }

    /**
     * Edits the original message of the specified {@link InteractionHook}
     *
     * @param interactionHook Non-null {@link InteractionHook}
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> editOriginal(@NonNull InteractionHook interactionHook) {
        return sendEx(null, interactionHook, false, true, null, null);
    }

    /**
     * Edits the original message of the specified {@link InteractionHook} with specified ephemeral
     *
     * @param interactionHook Non-null {@link InteractionHook}
     * @param ephemeral       Ephemeral
     * @return {@link RestAction} of {@link Message}
     */
    public RestAction<Message> editOriginal(@NonNull InteractionHook interactionHook, boolean ephemeral) {
        return sendEx(null, interactionHook.setEphemeral(ephemeral), ephemeral, true, null, null);
    }

    protected RestAction<Message> sendEx(MessageChannelUnion messageChannelUnion, InteractionHook interactionHook, boolean ephemeral,
        boolean editOriginal, Message messageToEdit, Message messageToReplyTo) {
        List<Button> buttons = new LinkedList<>();
        List<SelectOption> selectOptions = new LinkedList<>();

        interactions.forEach(((interaction, groupedInteractionEventConsumer) -> {
            if (interaction.isButton()) {
                buttons.add(interaction.getButton());
            } else if (interaction.isSelectOption()) {
                selectOptions.add(interaction.getSelectOption());
            }
        }));

        MessageCreateAction createMessageAction = null; // Channel#sendMessage() / Channel#sendMessage()
        MessageEditAction editMessageAction = null; // Channel#sendMessage() / Channel#editMessage()
        WebhookMessageCreateAction<Message> hookMessageAction = null; // InteractionHook#sendMessage()
        WebhookMessageEditAction<Message> hookMessageUpdateAction = null; // InteractionHook#editOriginal()

        MessageEditData messageEditData = messageEditBuilder.build();

        if (messageChannelUnion != null) {
            createMessageAction = messageChannelUnion.sendMessage(MessageCreateBuilder.fromEditData(messageEditData).build());
        } else if (interactionHook != null) {
            if (editOriginal) {
                hookMessageUpdateAction = interactionHook.setEphemeral(ephemeral).editOriginal(messageEditData);
            } else {
                hookMessageAction = interactionHook.setEphemeral(ephemeral).sendMessage(MessageCreateBuilder.fromEditData(messageEditData).build());
            }
        } else if (messageToEdit != null) {
            editMessageAction = messageToEdit.editMessage(messageEditData);
        } else {
            createMessageAction = messageToReplyTo.reply(MessageCreateBuilder.fromEditData(messageEditData).build());
        }

        List<ActionRow> actionRows = new LinkedList<>();

        if (!buttons.isEmpty()) {
            List<Button> fiveButtons = new ArrayList<>(5);

            for (Button button : buttons) {
                if (fiveButtons.size() == 5) {
                    actionRows.add(ActionRow.of(fiveButtons));

                    fiveButtons = new ArrayList<>(5);
                }

                fiveButtons.add(button);
            }

            actionRows.add(ActionRow.of(fiveButtons));
        } else {
            if (selectMenuBuilder != null) {
                if (!selectOptions.isEmpty()) {

                    if (selectMenuBuilder instanceof StringSelectMenu.Builder) {
                        ((StringSelectMenu.Builder) selectMenuBuilder).addOptions(selectOptions);
                    }
                }

                actionRows.add(ActionRow.of(selectMenuBuilder.build()));
            }
        }

        RestAction<Message> restAction;

        if (createMessageAction != null) {
            restAction = createMessageAction.setComponents(actionRows);
        } else if (editMessageAction != null) {
            restAction = editMessageAction.setComponents(actionRows);
        } else {
            if (hookMessageUpdateAction != null) {
                restAction = hookMessageUpdateAction.setComponents(actionRows);
            } else {
                restAction = hookMessageAction.setComponents(actionRows);
            }
        }

        InteractiveListener.addInteractable(this);

        return restAction;
    }

    //endregion

    /**
     * Gets all interactions of specified type
     *
     * @param interactionType Non-null {@link InteractionType}
     * @return Map of {@link Interaction} and its {@link Consumer} action
     */
    public Map<Interaction, Consumer<GroupedInteractionEvent>> getInteractionByType(InteractionType interactionType) {
        Map<Interaction, Consumer<GroupedInteractionEvent>> interactions = new HashMap<>();

        this.interactions.forEach((interaction, groupedInteractionEventConsumer) -> {
            if (interaction.getType() == interactionType) {
                interactions.put(interaction, groupedInteractionEventConsumer);
            }
        });

        return interactions;
    }

    /**
     * Processes the interaction event
     *
     * @param event Non-null {@link GroupedInteractionEvent}
     */
    @Override
    public void process(GroupedInteractionEvent event) {
        // Ignore modals
        if (event.isModalInteraction()) {
            return;
        }

        User user = event.getUser();

        // Ignore if user is null or cannot interact
        if (user == null || !canUserInteract(user)) {
            return;
        }

        // Entity select menu
        if (event.isEntitySelectMenuInteraction()) {
            EntitySelectInteractionEvent entitySelectInteractionEvent = event.getEntitySelectInteractionEvent();

            if (!id.toString().equals(entitySelectInteractionEvent.getComponentId())) {
                return;
            }

            entitySelectInteractionEventConsumer.accept(entitySelectInteractionEvent);
            return; // Can return, there will never be Interaction with entity
        }

        // String select menu
        if (event.isStringSelectMenuInteraction()) {
            StringSelectInteractionEvent stringSelectInteractionEvent = event.getStringSelectInteractionEvent();

            if (!id.toString().equals(stringSelectInteractionEvent.getComponentId())) {
                return;
            }

            stringSelectInteractionEventConsumer.accept(stringSelectInteractionEvent);
            // Cannot return, there might be Interaction with string select menu
        }

        for (Map.Entry<Interaction, Consumer<GroupedInteractionEvent>> entry : interactions.entrySet()) {
            Interaction interaction = entry.getKey();

            if (isApplicable(interaction, event)) {
                entry.getValue().accept(event);
            }
        }
    }
}
