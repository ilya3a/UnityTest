package com.yoyo.dbflusher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoyo.dbflusher.tracker.AnalyticsTracker


@Composable
fun TestTrackerScreen(
    tracker: AnalyticsTracker
) {
    val coroutineScope = rememberCoroutineScope()

    var eventName by remember { mutableStateOf("") }
    var params by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var counter by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Test analytics tracker"
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = eventName,
            onValueChange = { eventName = it },
            label = { Text("Event Name") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = params,
            onValueChange = { params = it },
            label = { Text("Parameters") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = {
                try {
                    tracker.logEvent(eventName, params)
                    counter++
                    message = "Event logged successfully"
                }catch (t:Throwable){
                    message = t.message.toString()
                }
            },
            enabled = eventName.isNotBlank() && params.isNotBlank(),
        ) {
            Text("Log Event")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "counter = ${counter}")

    }

}