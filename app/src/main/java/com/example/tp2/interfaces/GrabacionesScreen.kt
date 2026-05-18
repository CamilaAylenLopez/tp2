package com.example.tp2.interfaces

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tp2.media.SimpleAudioPlayer
import com.example.tp2.model.AudioNota

@Composable
fun GrabacionesScreen(navController: NavHostController, listaAudios: MutableList<AudioNota>){
    val context = LocalContext.current
    val player = remember { SimpleAudioPlayer() }

    var status by remember { mutableStateOf("Listo") }
    var audioSeleccionado by remember { mutableStateOf<AudioNota?>(null) }

    var isPlayerPrepared by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        onDispose {
            try{
                player.release()
            }catch (e: Exception){
            }
        }
    }

    Column(
        Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text("Grabaciones - Estado: ${status}")

        if (listaAudios.isEmpty()){
            Text("No hay ninguna grabación todavía")
            Spacer(modifier = Modifier.weight(1f))
        }
        else{
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(listaAudios){ audio ->
                    val esSeleccionado = audioSeleccionado?.id == audio.id

                    Button(
                        onClick = {
                            try {
                                player.stop()
                                isPlaying = false
                                isPlayerPrepared = false

                                audioSeleccionado = audio
                                val file = java.io.File(audio.ruta)

                                if (file.exists()){
                                    player.prepareFromFile(
                                        file = file,
                                        onCompleted = {
                                            status = "Terminado"
                                            isPlaying = false
                                        },
                                        onError = { msg ->
                                            status = "Error: $msg"
                                            isPlayerPrepared = false
                                            isPlaying = false
                                        }
                                    )
                                    isPlayerPrepared = true
                                    status = "Preparado: ${audio.nombre}"
                                }
                                else{
                                    status = "Error: Archivo no encontrado"
                                    Toast.makeText(context, "Archivo no encontrado", Toast.LENGTH_SHORT).show()
                                }
                            }catch (e: Exception){
                                status = "Error al cargar: ${e.message}"
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if(esSeleccionado) Color(0xFF6200EE) else Color(0xFFE0E0E0),
                            contentColor = if(esSeleccionado) Color.White else Color.Black
                        )
                    ){
                        Column(modifier = Modifier.padding(4.dp)) {
                            Text(audio.nombre, fontSize = 16.sp)
                            Text("ruta: ${audio.nameFromPath()}")
                            Text("Toca para seleccionar el audio", fontSize = 20.sp)
                        }
                    }
                }
            }
            if(audioSeleccionado != null){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                ) {
                    Text("Audio: ${audioSeleccionado?.nombre}")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Button(
                            onClick = {
                                try{
                                    player.play { status = it }
                                    isPlaying = true
                                    status = "Reproduciendo..."
                                }catch (e: Exception){
                                    status = "Error de reproducción: ${e.message}"
                                }
                        },
                            enabled = isPlayerPrepared && !isPlaying
                        ) { Text("Play") }

                        Button(
                            onClick = {
                                try {
                                    player.pause()
                                    isPlaying = false
                                    status = "Pausado"
                                }catch (e: Exception){
                                    status = "Error al pausar: ${e.message}"
                                }
                        },
                            enabled = isPlaying
                        ) { Text("Pause") }

                        Button(
                            onClick = {
                                try {
                                    player.stop()
                                    isPlaying = false
                                    isPlayerPrepared = false
                                    status = "Parado"
                                    audioSeleccionado = null
                                }catch (e: Exception){
                                    status = "Error al detener: ${e.message}"
                                }
                        },
                            enabled = isPlayerPrepared || isPlaying
                        ) { Text("Stop") }
                    }
                }
            }

        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {navController.navigate(Routes.AUDIO)},
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ir a grabar audio") }

        Button(
            onClick = {navController.popBackStack()},
            modifier = Modifier.fillMaxWidth()
        ) { Text("Volver para atrás") }
    }
}
fun AudioNota.nameFromPath(): String? {
    return this.ruta.split("/").lastOrNull()
}