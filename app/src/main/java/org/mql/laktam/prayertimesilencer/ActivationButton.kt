package org.mql.laktam.prayertimesilencer

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ActivationButton() {
    val context = LocalContext.current
    var isServiceRunning by remember { mutableStateOf(false) }

    val handleButtonClick = {
        if (isServiceRunning) {
            stopService(context)
            isServiceRunning = false
        } else {
            startService(context)
            isServiceRunning = true
        }
    }

    Column {
        Button(onClick = { handleButtonClick() }) {
            Text(if (isServiceRunning) "Stop Service" else "Start Service")
        }
    }
}


fun startService(context: Context) {
    val serviceIntent = Intent(context, MyForegroundService::class.java)
    ContextCompat.startForegroundService(context, serviceIntent)
}

fun stopService(context: Context) {
    val serviceIntent = Intent(context, MyForegroundService::class.java)
    context.stopService(serviceIntent)
}