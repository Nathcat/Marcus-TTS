import java.net.*;
import java.io.*;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.util.Base64;
/*
 * https://github.com/oscie57/tiktok-voice/wiki/Voice-Codes
 * for voice codes.
 */

public class Main {
	public static void main(String[] args) throws MalformedURLException, IOException {
		URL url = new URL("https://ottsy.weilbyte.dev/api/generation");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);

		String voice = "en_us_006";
		String text = "Hello world";

		if (args.length == 1) voice = args[0];
		else if (args.length == 2) { voice = args[0]; text = args[1]; }

		System.out.println("\"" + text + "\" in voice \"" + voice + "\"");

		String bodyString = "{\"voice\": \"" + voice + "\", \"text\": \"" + text +"\"}";
		try (OutputStream os = con.getOutputStream()) {
			byte[] input = bodyString.getBytes("utf-8");
			os.write(input, 0, input.length);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}
		
			JSONObject r = (JSONObject) new JSONParser().parse(response.toString());
			//System.out.println(r.get("data"));
			byte[] rB = Base64.getDecoder().decode((String) r.get("data"));
			FileOutputStream fos = new FileOutputStream(new File("tts.mp3"));
			fos.write(rB);
			fos.close();
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}

		System.out.println("Done");
	}
}
