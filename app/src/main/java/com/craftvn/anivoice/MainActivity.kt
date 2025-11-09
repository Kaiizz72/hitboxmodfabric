package com.craftvn.anivoice

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.foundation.selection.selectable
import androidx.compose.ui.semantics.Role

class MainActivity : ComponentActivity() {

    private val engine = VoiceEngine()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    VoiceChangerScreen(
                        onStart = { engine.start(it) },
                        onStop = { engine.stop() }
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        engine.stop()
    }
}

@Composable
fun VoiceChangerScreen(
    onStart: (VoiceMode) -> Unit,
    onStop: () -> Unit
) {
    val ctx = LocalContext.current
    var enabled by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(VoiceMode.ANIME_NU) }
    var permanentlyDenied by remember { mutableStateOf(false) }

    // Permission launcher
    val micPermissionLauncher = remember {
        androidx.activity.compose.rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            if (granted) {
                permanentlyDenied = false
                onStart(mode)
                enabled = true
            } else {
                // Check if permanently denied
                val shouldShow = androidx.activity.compose.LocalOnBackPressedDispatcherOwner.current
                permanentlyDenied = true
                enabled = false
            }
        }
    }

    LaunchedEffect(Unit) {
        // Ask on first open if not granted
        if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO)
            != PackageManager.PERMISSION_GRANTED) {
            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text("Anime Voice Changer", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

        // Enable switch
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = ctx.getString(R.string.enable_vc), style = MaterialTheme.typography.titleMedium)
            Switch(checked = enabled, onCheckedChange = { checked ->
                if (checked) {
                    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.RECORD_AUDIO)
                        == PackageManager.PERMISSION_GRANTED) {
                        onStart(mode)
                        enabled = true
                    } else {
                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    }
                } else {
                    onStop()
                    enabled = false
                }
            })
        }

        // Mode selector (Anime Nữ / Anime Nam)
        Text("Chọn kiểu giọng:", style = MaterialTheme.typography.titleSmall)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(
                VoiceMode.ANIME_NU to "Anime Nữ (cao & sáng)",
                VoiceMode.ANIME_NAM to "Anime Nam (trầm & ấm)"
            ).forEach { (m, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (mode == m),
                            onClick = {
                                mode = m
                                if (enabled) {
                                    onStart(mode) // restart with new mode
                                }
                            },
                            role = Role.RadioButton
                        )
                        .padding(vertical = 6.dp)
                ) {
                    RadioButton(selected = (mode == m), onClick = {
                        mode = m
                        if (enabled) onStart(mode)
                    })
                    Spacer(Modifier.width(8.dp))
                    Text(label)
                }
            }
        }

        if (permanentlyDenied) {
            Card {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = ctx.getString(R.string.permission_mic_title), fontWeight = FontWeight.SemiBold)
                    Text(text = ctx.getString(R.string.permission_mic_body))
                    Button(onClick = {
                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                            data = Uri.fromParts("package", ctx.packageName, null)
                        }
                        ctx.startActivity(intent)
                    }) {
                        Text(ctx.getString(R.string.open_settings))
                    }
                }
            }
        }

        Spacer(Modifier.weight(1f))
        Text(
            "Lưu ý: Trên Android, việc thay đổi giọng trong cuộc gọi hệ thống thường bị hạn chế. Ứng dụng này xuất âm đã chuyển đổi ra loa tai nghe của máy khi bật.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}