package com.audioalive

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.tv.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Surface
import com.audioalive.service.AudioKeepAliveService
import com.audioalive.ui.theme.AudioAliveTheme

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalTvMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.POST_NOTIFICATIONS), 101)
            }
        }

        setContent {
            AudioAliveTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    shape = RectangleShape
                ) {
                    MainScreen(
                        onStartServiceClick = {
                            initAudioKeepAliveService()
                        }
                    )
                }
            }
        }
    }

    private fun initAudioKeepAliveService() {
        val serviceIntent = Intent(this, AudioKeepAliveService::class.java)
        startForegroundService(serviceIntent)
        Toast.makeText(this, getString(R.string.main_toast_service_started), Toast.LENGTH_SHORT).show()
        finish()
    }
}

@Composable
@OptIn(ExperimentalTvMaterial3Api::class)
fun MainScreen(
    onStartServiceClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Button(
            onClick = onStartServiceClick,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = stringResource(R.string.main_btn_start_service))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    AudioAliveTheme {
        MainScreen(onStartServiceClick = {})
    }
}
