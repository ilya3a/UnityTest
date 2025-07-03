package com.yoyo.dbflusher.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.yoyo.concurrenteventtracker.data.db.AnalyticsEvent
import com.yoyo.concurrenteventtracker.tracker.AnalyticsTracker
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch


@Composable
fun TestTrackerScreen(
    tracker: AnalyticsTracker,
) {
    val coroutineScope = rememberCoroutineScope()

    var message by remember { mutableStateOf("") }

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

        Button(
            onClick = {

                coroutineScope.launch {
                    try {
                        tracker.trackEvent(AnalyticsEvent(name = "TEST_EVENT"))
                        message = "Event logged successfully"
                    } catch (t: Throwable) {
                        message = t.message.toString()
                    }
                }


            }) {
            Text("Log Event")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(text = message)

        Spacer(modifier = Modifier.height(16.dp))


        Button(
            onClick = {
                try {
                    coroutineScope.launch(IO) {
                        tracker.uploadFlushedEvents()
                    }


                } catch (t: Throwable) {
                    message = t.message.toString()
                }
            }
        ) {
            Text("upload Events")
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = {
                coroutineScope.launch {
                    try {
                        tracker.shutdown()
                    } catch (t: Throwable) {
                        message = t.message.toString()
                    }
                }
            }

        ) {
            Text("ShutDown")
        }
    }

}