package net.nathcat.marcus_tts.JDA;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.FileUpload;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import net.nathcat.marcus_tts.Utils;
import net.nathcat.marcus_tts.exceptions.APIException;

public class MarcusListener extends ListenerAdapter {
    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (event.getName().equals("test")) {
            System.out.println("Got test command.");

            byte[] buffer;
            try {
                buffer = Utils.getTTS("Hello world");
            } catch (IOException | ParseException | APIException e) {
                event.reply(e.getMessage()).queue();
                System.err.println("Failed to get TTS from API: " + e.toString());
                e.printStackTrace();
                return;
            }

            System.out.println("Got TTS from API");

            FileUpload fileUpload = FileUpload.fromData(buffer, "marcus-test.mp3");
            System.out.println("Created file upload");

            MessageCreateData msg = new MessageCreateBuilder()
                .setVoiceMessage(true)
                .setFiles(fileUpload)
                .build();

            event.reply(msg).queue();

            System.out.println("Queued message. Done.");
        }
        else if (event.getName().equals("say")) {
            System.out.println("Got Say command.");

            byte[] buffer;
            try {
                buffer = Utils.getTTS(event.getOption("message").getAsString());
            } catch (IOException | ParseException | APIException e) {
                event.reply(e.getMessage()).queue();
                System.err.println("Failed to get TTS from API: " + e.toString());
                e.printStackTrace();
                return;
            }

            System.out.println("Got TTS from API");

            FileUpload fileUpload = FileUpload.fromData(buffer, "marcus-test.mp3");
            System.out.println("Created file upload");

            MessageCreateData msg = new MessageCreateBuilder()
                .setVoiceMessage(true)
                .setFiles(fileUpload)
                .build();

            event.reply(msg).queue();

            System.out.println("Queued message. Done.");
        }
    } 
}
