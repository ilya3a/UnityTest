package com.yoyo.dbflusher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.yoyo.concurrenteventtracker.tracker.AnalyticsTracker
import com.yoyo.dbflusher.ui.theme.DBflusherTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DBflusherTheme {
                Box(modifier = Modifier.fillMaxSize()) {
                    TestTrackerScreen(analyticsTracker)
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            analyticsTracker.shutdown()
        }

    }
}
