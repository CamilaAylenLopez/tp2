package com.example.tp2.interfaces

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tp2.model.AudioNota
import com.example.tp2.media.SimpleAudioPlayer
import com.example.tp2.media.SimpleAudioRecorder
import com.example.tp2.storage.AppFiles
import java.io.File
import java.util.UUID

@Composable
fun AudioScreen(navController: NavHostController, listaAudios: MutableList<AudioNota>){
    val context = LocalContext.current
    //var newAudioFile by remember { mutableStateOf<java.io.File?>(null) }
    var newAudioFile by remember { mutableStateOf<File?>(null)}

    //estado de permisso
    val (audioGranted, requestAudio) = rememberAudioPermission()

    var status by remember { mutableStateOf("Listo") }
    var isRecording by remember {mutableStateOf(false)}
    var isPlayerPrepared by remember {mutableStateOf(false)}
    var isPlaying by remember {mutableStateOf(false)}

    val player = remember { SimpleAudioPlayer() }
    val recorder = remember { SimpleAudioRecorder() }

    DisposableEffect(Unit) {
        //cuando se reinicie la inetrfaz
        onDispose {
            try {
                recorder.stop()
                player.release()
            }catch (e: Exception){
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Audio - Reproductor")

        Text("Archivo: ${newAudioFile?.name ?: "Ninguno"} (${if (newAudioFile?.exists() == true) "existe" else "no existe"})")
        Text("Estado: $status")
        Text(if (audioGranted) "Permiso de microfono concedido" else "Permiso de microfono denegado")

        if(!audioGranted){
            Button(onClick = {requestAudio()}) {
                Text("Solicitar microfono")
            }
        }
        else{
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        try{
                            player.stop()
                            isPlaying = false
                            isPlayerPrepared = false

                            val archivoNuevo = AppFiles.newAudioFile(context)
                            newAudioFile = archivoNuevo

                            recorder.start(archivoNuevo) {status = it}
                            isRecording = true
                            status = "Grabando..."
                        }catch (e: Exception){
                            status = "Error al iniciar grabación: ${e.message}"
                        }
                    },
                    enabled = !isRecording
                ) { Text("Grabar audio") }

                Button(onClick = {
                    try{
                        recorder.stop()
                        isRecording = false
                        status = "Grabación guardada"

                        newAudioFile?.let { archivo ->
                            val nuevaNota = AudioNota(
                                id = UUID.randomUUID().toString(),
                                nombre = "Audio ${listaAudios.size + 1}",
                                ruta = archivo.absolutePath,
                                fecha = System.currentTimeMillis()
                            )
                            listaAudios.add(nuevaNota)
                            Toast.makeText(context, "Audio agregado a la lista", Toast.LENGTH_SHORT).show()
                        }
                    }catch (e: Exception){
                        status = "Error al detener grabación: ${e.message}"
                        isRecording = false
                    }
                },
                    enabled = isRecording
                ) { Text("Stop grabación") }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ){
                Button(onClick = {
                    newAudioFile?.let { archivo ->
                        try {
                            player.prepareFromFile(
                                file = archivo,
                                onCompleted = {
                                    status = "Reproducción terminada"
                                    isPlaying = false
                                },
                                onError = { msg ->
                                    status = "Error: $msg"
                                    isPlaying = false
                                    isPlayerPrepared = false
                                }
                            )
                            isPlayerPrepared = true
                            status = "Audio listo para reproducir"
                        }catch (e: Exception){
                            status = "Error al preparar archivo: ${e.message}"
                        }
                    } ?: run { status = "Error: Graba algo primero" }
                },
                    enabled = !isRecording && newAudioFile?.exists() == true
                ){ Text("Preparar")}

                Button(onClick = {
                    try {
                        player.play { status = it }
                        isPlaying = true
                        status = "Reproduciondo..."
                    }catch (e: Exception){
                        status = "Error de reproducción: ${e.message}"
                    }
                },
                    enabled = isPlayerPrepared && !isPlaying
                ) { Text("Play") }

                Button(onClick = {
                    try {
                        player.pause()
                        isPlaying = false
                        status = "Pausado"
                    } catch (e: Exception){
                        status = "Error al pausar: ${e.message}"
                    }
                },
                    enabled = isPlaying
                ) { Text("Pause") }

                Button(onClick = {
                    try {
                        player.stop()
                        isPlaying = false
                        status = "Reproducción detenida"
                    }catch (e: Exception){
                        status = "Error al detener: ${e.localizedMessage}"
                    }
                },
                    enabled = isPlayerPrepared || isPlaying
                ) { Text("Stop") }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {navController.navigate(Routes.GRABACIONES)},
            enabled = !isRecording,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Ver grabaciones") }


        Button(
            onClick = {navController.popBackStack()},
            enabled = !isRecording,
            modifier = Modifier.fillMaxWidth()
        ){ Text("Volver para atrás") }
    }
}