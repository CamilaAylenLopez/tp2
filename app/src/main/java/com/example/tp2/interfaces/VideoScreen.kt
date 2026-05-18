package com.example.tp2.interfaces

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.tp2.R

@Composable
fun VideoScreen(navController: NavHostController){

    val context = LocalContext.current

    var status by remember { mutableStateOf("Video no cargado") }

    var isVideoReady by remember { mutableStateOf(false) }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                status = when(playbackState){
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> {
                        isVideoReady = true
                        "READY"
                    }
                    Player.STATE_ENDED -> "ENDED"
                    else -> "?"
                }
            }
        }
        player.addListener(listener)

        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Video - Exoplayer (Media 3)")
        Text("Estado de player:${status}")

        AndroidView(
            modifier = Modifier.fillMaxWidth().weight(1f),
            factory = {
                PlayerView(it).apply {
                    this.player = player
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Button(onClick = {
                try {
                    status = "Cargando..."
                    val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_video}")
                    val item = MediaItem.fromUri(uri)
                    player.setMediaItem(item)
                    player.prepare()
                }catch (e: Exception){
                    status = "Error al cargar: ${e.message}"
                }
            }) {Text("Cargar") }

            Button(
                onClick = {
                    player.play()
                    status = "Play"
                },
                enabled = isVideoReady,
                modifier = Modifier.weight((1f))
            ) { Text("Play")}

            Button(
                onClick = {
                    player.pause()
                    status = "Pause"
                },
                enabled = isVideoReady,
                modifier = Modifier.weight((1f))
            ) { Text("Pause")}

            Button(
                onClick = {
                    player.seekTo(0)
                    status = "Rewind"
                },
                enabled = isVideoReady,
                modifier = Modifier.weight((1f))
            ) { Text("Reiniciar")}
        }

        Button(
            onClick = {navController.popBackStack()},
            modifier = Modifier.fillMaxWidth()
        ) { Text("Volver para atrás") }
    }
}