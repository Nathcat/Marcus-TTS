package net.nathcat.marcus_tts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import net.nathcat.marcus_tts.exceptions.APIException;

public class Utils {
    /**
     * This is the voice code to send to the API.
     */
    private static final String VOICE_CODE = "en_male_narration";
    public static final int KBPS_BIT_RATE = 128;
    public static final JSONParser jsonParser = new JSONParser();

    /**
     * Get a byte array containing the MP3 TTS data from the API. 
     * @param text The text to convert to speech
     * @return The TTS MP3
     * @throws IOException Thrown if any I/O errors occur in communication with the API
     * @throws ParseException Thrown if the response from the API is not valid JSON
     * @throws APIException 
     */
    public static byte[] getTTS(String text) throws IOException, ParseException, APIException {
        // Setup the API connection
        URL url = new URL("https://ottsy.weilbyte.dev/api/generation");
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Content-Type", "application/json");
		con.setDoOutput(true);

        // Send the request to the API
        String bodyString = "{\"voice\": \"" + VOICE_CODE + "\", \"text\": \"" + text +"\"}";
		OutputStream os = con.getOutputStream();
		byte[] input = bodyString.getBytes("utf-8");
		os.write(input, 0, input.length);	

        // Read the response from the API
        JSONObject r = (JSONObject) readJSON(con.getInputStream());
        if (r.get("success").equals(false)) {
            throw new APIException(r.get("error").toString());
        }

		byte[] rB = Base64.getDecoder().decode((String) r.get("data"));
        
        os.close();
        
        return rB;
    }
    /**
     * Read a JSON string from an input stream
     * @param in The input stream to read from
     * @return The parsed object obtained from the stream
     * @throws IOException
     * @throws ParseException
     */
    public static Object readJSON(InputStream in) throws IOException, ParseException {
        BufferedReader br = new BufferedReader(new InputStreamReader(in, "utf-8"));
        StringBuilder response = new StringBuilder();
		String responseLine = null;
		while ((responseLine = br.readLine()) != null) {
			response.append(responseLine.trim());
		}

        br.close();
        return jsonParser.parse(response.toString());
    } 
}
