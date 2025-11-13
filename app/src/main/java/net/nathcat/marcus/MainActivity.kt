package net.nathcat.marcus

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import net.nathcat.marcus.ui.theme.MarcusTheme
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity(
    private val mediaPlayer: MediaPlayer = MediaPlayer(),
) : ComponentActivity() {
    @Throws(IOException::class, ParseException::class)
    fun fileTTS(text: String): File {
        val audio = GetTTS(text)
        val file = File.createTempFile("marcus", ".mp3", cacheDir)
        file.deleteOnExit()
        val fos = FileOutputStream(file)
        fos.write(audio)
        fos.close()

        return file
    }

    fun obtainTTS(text: String, callback: (File) -> Unit) {
        try {
            val file = fileTTS(text)

            this@MainActivity.runOnUiThread {
                callback(file)
            }
        }
        catch (e: IOException) {
            this@MainActivity.runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to communicate with API: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }

            e.printStackTrace()
        }
        catch (e: ParseException) {
            this@MainActivity.runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Failed to parse response from API",
                    Toast.LENGTH_LONG
                ).show()
            }

            e.printStackTrace()
        }
    }

    fun doTTS(text: String) {
        if (text == "") {
            this@MainActivity.runOnUiThread {
                Toast.makeText(
                    this@MainActivity,
                    "Text is empty!",
                    Toast.LENGTH_SHORT
                ).show()
            }

            return
        }

        obtainTTS(text) { file ->
            mediaPlayer.reset()

            val fis = FileInputStream(file)
            mediaPlayer.setDataSource(fis.fd)
            mediaPlayer.prepare()
            mediaPlayer.start()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MarcusTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Column(
                            modifier = Modifier
                                .align(Alignment.TopStart)
                                .padding(PaddingValues(
                                    top = 100.dp,
                                    start = 10.dp,
                                    end = 10.dp
                                ))
                        ) {
                            Text(
                                text = "Marcus TTS",
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "The following section can be used to say a custom phrase with the" +
                                        " Marcus TTS.",
                                style = MaterialTheme.typography.bodyMedium
                            )

                            var text by remember { mutableStateOf(TextFieldValue("")) }

                            TextField(
                                value = text,
                                onValueChange = { text = it },
                                label = { Text("Text to convert") },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Go
                                ),
                                keyboardActions = KeyboardActions(
                                    onGo = {
                                        Thread { doTTS(text.text) }.start()
                                    }
                                )
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Button(
                                    content = { Text("Say as Marcus") },
                                    onClick = {
                                        Thread { doTTS(text.text) }.start()
                                    }
                                )

                                IconButton(
                                    content = { Icon(
                                        imageVector = Icons.Filled.Share,
                                        contentDescription = "Share your phrase",
                                        modifier = Modifier.fillMaxWidth()
                                    ) },
                                    onClick = {
                                        Thread {
                                            obtainTTS(text.text) { file ->
                                                val uri = try { AudioFileProvider.getUriForFile(
                                                    this@MainActivity,
                                                    "net.nathcat.marcus.fileprovider",
                                                    file
                                                ) }
                                                catch (e: IllegalArgumentException) {
                                                    this@MainActivity.runOnUiThread {
                                                        Toast.makeText(
                                                            this@MainActivity,
                                                            "This clip could not be shared!",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                        e.printStackTrace()
                                                    }

                                                    return@obtainTTS
                                                }

                                                val shareIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(Intent.EXTRA_STREAM, uri)
                                                    type = "audio/mpeg"
                                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                                }

                                                startActivity(
                                                    Intent.createChooser(
                                                        shareIntent,
                                                        "Share your Marcus phrase"
                                                    )
                                                )
                                            }
                                        }.start()
                                    }
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(PaddingValues(
                                    top = 10.dp,
                                    bottom = 10.dp
                                ))
                            )

                            Text(
                                text = "Common Marcus phrases",
                                style = MaterialTheme.typography.titleMedium
                            )

                            Button(
                                content = { Text("Hello Robert") },
                                onClick = {
                                    Thread { doTTS("Hello Robert") }.start()
                                }
                            )

                            Button(
                                content = { Text("I will now launder 17000 dollars through a locally owned Greek restaurant") },
                                onClick = {
                                    Thread { doTTS("I will now launder 17000 dollars through a locally owned Greek restaurant") }.start()
                                }
                            )
                        }

                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(PaddingValues(
                                    top = 5.dp,
                                    bottom = 25.dp
                                ))
                        ) {
                            Text(
                                text = VERSION_STRING,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = Color(200, 200, 200)
                                ),
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            )
                        }
                    }
                }
            }
        }

        Thread {
            val message = GetMessages()
            if (message != null) {
                this@MainActivity.runOnUiThread {
                    Toast.makeText(
                        this@MainActivity,
                        message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }.start()
    }
}