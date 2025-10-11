package enterprises.iwakura.jdainteractables;

import java.io.InputStream;

import enterprises.iwakura.jdainteractables.InteractionHandler.Result;
import enterprises.iwakura.jdainteractables.components.InteractableMessage;
import enterprises.iwakura.jdainteractables.components.InteractableModal;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.components.actionrow.ActionRow;
import net.dv8tion.jda.api.components.label.Label;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu;
import net.dv8tion.jda.api.components.selections.EntitySelectMenu.SelectTarget;
import net.dv8tion.jda.api.components.selections.SelectOption;
import net.dv8tion.jda.api.components.selections.StringSelectMenu;
import net.dv8tion.jda.api.components.textdisplay.TextDisplay;
import net.dv8tion.jda.api.components.textinput.TextInput;
import net.dv8tion.jda.api.components.textinput.TextInputStyle;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command.Type;
import net.dv8tion.jda.api.modals.Modal;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.dv8tion.jda.internal.interactions.CommandDataImpl;

@Slf4j
public class MainTest extends ListenerAdapter {

    public static void main(String[] args) throws InterruptedException {
        var jda = JDABuilder.createDefault("",
            GatewayIntent.MESSAGE_CONTENT).build();

        jda.addEventListener(new InteractableListener());
        jda.addEventListener(new MainTest());

        jda.upsertCommand(
            CommandDataImpl.of(Type.SLASH, "test-jda-interactables", "Test command for JDA Interactables")).queue();

        log.info("Logging into Discord...");
        jda.awaitReady();

        log.info("Sleeping...");
        Thread.sleep(Integer.MAX_VALUE);
    }

    private static FileUpload getResourceAsFileUpload(String path) {
        final int lastSeparatorIndex = path.lastIndexOf('/');
        final String fileName = path.substring(lastSeparatorIndex + 1);

        final InputStream stream = MainTest.class.getResourceAsStream(path);
        if (stream == null) {
            throw new IllegalArgumentException("Could not find resource at: " + path);
        }

        return FileUpload.fromData(stream, fileName);
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent slashEvent) {
        log.info("Received slash command interaction: {}/{}", slashEvent.getName(), slashEvent.getSubcommandName());

InteractableModal interactableModal = new InteractableModal(event -> {
    String name = event.getValue("name").getAsString();
    String reason = event.getValue("reason").getAsString();
    String math = event.getValue("math").getAsString();

    event.reply("Thank you for your application!\n"
        + "Name: " + name + "\n"
        + "Reason: " + reason + "\n"
        + "Math: " + math).queue();
    return Result.REMOVE;
});

Modal.Builder modalBuilder = Modal.create("random-id", "Job Application")
    .addComponents(
        TextDisplay.of("# Welcome!\nPlease, fill out the form below."),
        Label.of(
            "Name",
            TextInput.of("name", TextInputStyle.SHORT)
        ),
        Label.of(
            "Why are you interested in this position?",
            TextInput.of("reason", TextInputStyle.PARAGRAPH)
        ),
        Label.of(
            "What's 1+1?",
            TextInput.of("math", TextInputStyle.SHORT)
        )
    );

interactableModal.useModal(modalBuilder);

slashEvent.replyModal(modalBuilder.build())
    .queue(interactableModal.registerOnCompleted());
    }
}
