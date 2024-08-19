package org.mql.laktam.prayertimesilencer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import org.mql.laktam.prayertimesilencer.ui.theme.PrayerTimeSilencerTheme

import android.content.Context
import android.media.AudioManager
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import org.mql.laktam.prayertimesilencer.ui.theme.PrayerTimeSilencerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PrayerTimeSilencerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
//                    Greeting(
//                        name = "Silence",
//                        modifier = Modifier.padding(innerPadding)
//                    )
                    SilenceButton(
                        context = this,
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Composable
fun SilenceButton(context: Context, modifier: Modifier = Modifier) {
    Button(
        onClick = {
            val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            audioManager.ringerMode = AudioManager.RINGER_MODE_SILENT
        },
        modifier = modifier
    ) {
        Text(text = "Silence Phone")
    }
}


@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    PrayerTimeSilencerTheme {
        Greeting("Android")
    }
}