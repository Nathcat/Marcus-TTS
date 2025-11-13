package net.nathcat.marcus

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.json.simple.parser.ParseException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.StandardCharsets
import java.util.Base64

const val VERSION_STRING: String = "Marcus TTS v1.1.0 - Nathcat"
const val VERSION_CODE: Int = 2

/**
 * Read JSON from an input stream
 */
@Throws(IOException::class, ParseException::class)
fun readJSON(stream: InputStream): JSONObject {
    val br = BufferedReader(InputStreamReader(stream))
    val builder = StringBuilder()

    var line: String? = br.readLine()
    while (line != null) {
        builder.append(line)
        line = br.readLine()
    }

    return JSONParser().parse(builder.toString()) as JSONObject
}

/**
 * Use the API to transform a string into a TTS message
 */
@Throws(IOException::class, ParseException::class)
fun GetTTS(text: String): ByteArray {
    val url = URL("https://ottsy.weilbyte.dev/api/generation")
    val con = url.openConnection() as HttpURLConnection
    con.requestMethod = "POST"
    con.setRequestProperty("Content-Type", "application/json")
    con.doOutput = true

    val bodyString = "{\"voice\": \"en_male_narration\", \"text\": \"$text\"}"
    val os = con.getOutputStream()
    os.write(bodyString.toByteArray(StandardCharsets.UTF_8))

    val response = readJSON(con.getInputStream())
    if (response["success"]!! == false) {
        throw APIException(response["error"] as String)
    }

    val rB = Base64.getDecoder().decode(response["data"]!! as String)

    os.close()
    return rB
}

fun GetMessages(): String? {
    val url = URL("https://nathcat.net/static/docs/Marcus-TTS/app-messages.php?v=$VERSION_CODE")
    val con = url.openConnection() as HttpURLConnection
    con.requestMethod = "GET"
    con.doOutput = true

    val response = String(con.getInputStream().readAllBytes())

    return if (response == "N/A") { null } else { response }
}