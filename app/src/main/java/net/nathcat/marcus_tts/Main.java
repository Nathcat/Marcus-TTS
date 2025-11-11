package net.nathcat.marcus_tts;

import java.io.*;
import java.util.ArrayList;
/*
 * https://github.com/oscie57/tiktok-voice/wiki/Voice-Codes
 * for voice codes.
 */
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.nathcat.marcus_tts.JDA.MarcusListener;
import net.nathcat.marcus_tts.exceptions.APIException;

public class Main {
	public static String APP_ID;
	public static String DISCORD_TOKEN;
	public static String PUBLIC_KEY;
	public static final String CONFIG_PATH = "Assets/conf.json";

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, APIException {
		JSONObject config = (JSONObject) Utils.readJSON(new FileInputStream(new File(CONFIG_PATH)));
		APP_ID = (String) config.get("appId");
		DISCORD_TOKEN = (String) config.get("discordToken");
		PUBLIC_KEY = (String) config.get("publicKey");

		JDA jda = JDABuilder
			.createDefault(DISCORD_TOKEN)
			.enableIntents(GatewayIntent.MESSAGE_CONTENT)
			.addEventListeners(new MarcusListener())
			.build();
			
		List<CommandData> commands = new ArrayList<>();
		commands.add(Commands.slash("test", "Simple hello world test"));
		commands.add(Commands.slash("say", "Have Marcus say something")
			.addOption(OptionType.STRING, "message", "This is what Marcus will say")
		);

		jda.updateCommands().addCommands(commands).queue();

		System.out.println("Setup done.");
	}
}
