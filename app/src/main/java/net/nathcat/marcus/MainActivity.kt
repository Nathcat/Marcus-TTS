package net.nathcat.marcus

import android.media.MediaPlayer
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import net.nathcat.marcus.ui.theme.MarcusTheme
import org.json.simple.parser.ParseException
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

class MainActivity(
    private val mediaPlayer: MediaPlayer = MediaPlayer()
) : ComponentActivity() {
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

        try {
            val audio = GetTTS(text)

            this@MainActivity.runOnUiThread {
                val file = File.createTempFile("marcus", "mp3", cacheDir)
                file.deleteOnExit()
                val fos = FileOutputStream(file)
                fos.write(audio)
                fos.close()

                mediaPlayer.reset()

                val fis = FileInputStream(file)
                mediaPlayer.setDataSource(fis.fd)
                mediaPlayer.prepare()
                mediaPlayer.start()
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
                                    start = 10.dp
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

                            Button(
                                content = { Text("Say as Marcus") },
                                onClick = {
                                    Thread { doTTS(text.text) }.start()
                                }
                            )

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
                    }
                }
            }
        }
    }
}