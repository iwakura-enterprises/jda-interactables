package enterprises.iwakura.jdainteractables;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import enterprises.iwakura.jdainteractables.InteractionHandler.Result;
import enterprises.iwakura.jdainteractables.components.InteractableMessage;
import enterprises.iwakura.jdainteractables.components.InteractableModal;
import lombok.extern.slf4j.Slf4j;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.buttons.ButtonStyle;
import net.dv8tion.jda.api.components.container.Container;
import net.dv8tion.jda.api.components.filedisplay.FileDisplay;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.mediagallery.MediaGallery;
import net.dv8tion.jda.api.components.mediagallery.MediaGalleryItem;
import net.dv8tion.jda.api.components.section.Section;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.separator.Separator;
import net.dv8tion.jda.api.components.separator.Separator.Spacing;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.thumbnail.Thumbnail;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageEditBuilder;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Slf4j
public class MainTest extends ListenerAdapter {

    public static void main(String[] args) throws InterruptedException {
        var jda = JDABuilder.createDefault("", GatewayIntent.MESSAGE_CONTENT).build();

        jda.addEventListener(new InteractableListener());
        jda.addEventListener(new MainTest());

        jda.upsertCommand(CommandDataImpl.of(Type.SLASH, "test-jda-interactables", "Test command for JDA Interactables")).queue();

        log.info("Logging into Discord...");
        jda.awaitReady();

        log.info("Sleeping...");
        Thread.sleep(Integer.MAX_VALUE);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent slashEvent) {
        log.info("Received slash command interaction: {}/{}", slashEvent.getName(), slashEvent.getSubcommandName());

        /*
        InteractableModal interactableModal = new InteractableModal(event -> {
            var modalMapping = event.getValue("role-select");
            if (modalMapping == null) {
                event.reply("No role selected!").queue();
            } else {
                event.reply("You requested the role: " + modalMapping.getAsMentions().getRoles()).queue();
            }
            return Result.REMOVE;
        });

        var modalBuilder = Modal.create("modal", "Role Request Form")
            .addComponents(
                TextDisplay.of("""
            **Welcome!**
            
            Please read the following before continuing:
            
            - Select a user to request a role for
            - A moderator will review your request
            - Abuse of this system may result in penalties
            """),
                Label.of(
                    "Type",
                    EntitySelectMenu.create("role-select", SelectTarget.ROLE).setMaxValues(2).build()
                ).withUniqueId(10)
            );

        interactableModal.useModal(modalBuilder);

        slashEvent.replyModal(modalBuilder.build())
            .queue(interactableModal.registerOnCompleted());
*/
        InteractableMessage interactableMessage = new InteractableMessage();

        var musicOption = interactableMessage.addInteraction(Interaction.asSelectOption("Music"), (event) -> {
            //event.reply("Selected music!").queue();
            log.info("Selected music option");
            return Result.KEEP;
        });

        var funOption = interactableMessage.addInteraction(Interaction.asSelectOption("Fun"), (event) -> {
            //event.reply("Selected fun!").queue();
            log.info("Selected fun option");
            return Result.KEEP;
        });

        interactableMessage.addInteractionRule(InteractionRules.denyUsers(slashEvent.getUser()));
        interactableMessage.addInteractionDeniedCallback(InteractionDeniedCallbacks.deferReply(reply -> {
            reply.deferReply(true).setContent("You cannot interact with this message!").queue();
        }));

        var entitySelectMenu = interactableMessage.addInteraction(
            Interaction.asEntitySelectMenu(EntitySelectMenu.create("foo", SelectTarget.ROLE)
                .setMinValues(2)
                .setMaxValues(5)
                .setRequired(true)),
            (event) -> {
                var roles = event.getValues();
                if (roles.isEmpty()) {
                    event.reply("No roles selected!").queue();
                } else {
                    StringBuilder roleNames = new StringBuilder();
                    for (var role : roles) {
                        roleNames.append(((Role)role).getName()).append(", ");
                    }
                    // Remove last comma and space
                    roleNames.setLength(roleNames.length() - 2);
                    event.reply("You selected the following roles: " + roleNames).queue();
                }
                return Result.KEEP;
        });

        // A simple box; looks similar to an embed but...
        Container container = Container.of(
            // Displays content on the left and an "accessory" on the right.
            Section.of(
                // A thumbnail, it should work with all image formats Discord supports.
                // You can make it a spoiler and also give it a description (alternative text)
                Thumbnail.fromFile(getResourceAsFileUpload("/cv2.png")),
                // The section's children
                TextDisplay.of("## A container"),
                TextDisplay.of("Quite different from embeds"),
                TextDisplay.of("-# You can even put small text")
            ),

            // A separator; can be made invisible or be larger.
            Separator.createDivider(Spacing.LARGE),

            // Another section, note that you can have at most 3 children (excluding the accessory).
            // You're always free to use newlines in your text displays,
            // but keep in mind a new TextDisplay will display as a different paragraph.
            Section.of(
                // For the sake of the example, this button will do nothing.
                interactableMessage.addInteraction(Interaction.asButton(ButtonStyle.PRIMARY, "Do something!"), (event) -> {
                    event.reply("Clicked!").queue();
                    return Result.KEEP;
                }),
                TextDisplay.of("**Moderation:** Moderates the messages"),
                TextDisplay.of("**Status:** Enabled")
            ),
            // A row of actionable components.
            ActionRow.of(
                // For the sake of the example, this select menu will do nothing.
                interactableMessage.addInteraction(Interaction.asStringSelectMenu("Select values...", 2, 10, musicOption, funOption), (event) -> {
                    // This will only be called if the user selected "Music" or "Fun"
                    var selected = event.getValues();
                    event.reply("You selected: " + selected).queue();
                    return Result.KEEP;
                })
            ).withUniqueId(42), // Set an identifier, this may be useful to specifically remove this action row later

            ActionRow.of(
                // For the sake of the example, this select menu will do nothing.
                entitySelectMenu
            ).withUniqueId(123), // Set an identifier, this may be useful to specifically remove this action row later

            // Separate things a bit.
            Separator.createDivider(Separator.Spacing.LARGE),

            // Another text display, you are not limited per-component,
            // there is only a character limit for the whole message (see [[Message#MAX_CONTENT_LENGTH_COMPONENT_V2]]).
            TextDisplay.of("Download the current configuration:"),
            // Displays a simple download component, has no preview.
            FileDisplay.fromFile(FileUpload.fromData("{}".getBytes(StandardCharsets.UTF_8), "config.json")),

            // A set of pictures to display, display in a mosaic
            // It can also take one item, in which case it will take the most horizontal space as possible,
            // depending on the aspect ratio.
            MediaGallery.of(
                MediaGalleryItem.fromFile(getResourceAsFileUpload("/docs.gif"))
            )
        );

        // No need to upload files here, it's taken care of automatically
        slashEvent.replyComponents(container)
            // This is required any time you are using Components V2
            .useComponentsV2()
            .queue(interactableMessage.registerOnCompleted());
    }

    private static FileUpload getResourceAsFileUpload( String path)
    {
        final int lastSeparatorIndex = path.lastIndexOf('/');
        final String fileName = path.substring(lastSeparatorIndex + 1);

        final InputStream stream = MainTest.class.getResourceAsStream(path);
        if (stream == null)
            throw new IllegalArgumentException("Could not find resource at: " + path);

        return FileUpload.fromData(stream, fileName);
    }
}
