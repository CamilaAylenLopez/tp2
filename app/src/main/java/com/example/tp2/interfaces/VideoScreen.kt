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

    var status by remember { mutableStateOf("Listo") }

    val player = remember {
        ExoPlayer.Builder(context).build()
    }

    DisposableEffect(Unit) {
        val listener = object : Player.Listener{
            override fun onPlaybackStateChanged(playbackState: Int) {
                status = when(playbackState){
                    Player.STATE_IDLE -> "IDLE"
                    Player.STATE_BUFFERING -> "BUFFERING"
                    Player.STATE_READY -> "READY"
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
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = {
                val uri = Uri.parse("android.resource://${context.packageName}/${R.raw.sample_video}")
                val item = MediaItem.fromUri(uri)
                player.setMediaItem(item)
                player.prepare()
            }) {Text("Cargar") }
            Button(onClick = {player.play();status = "Play"}) { Text("Play")}
            Button(onClick = {player.pause();status = "Pause"}) { Text("Pause")}
            Button(onClick = {player.seekTo(0);status = "Rewind"}) { Text("Reiniciar")}
        }

        Button(
            onClick = {navController.popBackStack()})
        { Text("Volver para atrás") }
    }
}