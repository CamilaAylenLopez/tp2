package com.example.tp2.interfaces

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import java.util.UUID

@Composable
fun AudioScreen(navController: NavHostController, listaAudios: MutableList<AudioNota>){
    val context = LocalContext.current
    //var newAudioFile = remember { AppFiles.newAudioFile((context)) }
    var newAudioFile by remember { mutableStateOf<java.io.File?>(null) }

    //estado de permisso
    val (audioGranted, requestAudio) = rememberAudioPermission()

    var status by remember { mutableStateOf("Listo") }
    val player = remember { SimpleAudioPlayer() }
    val recorder = remember { SimpleAudioRecorder() }

    DisposableEffect(Unit) {
        //cuando se reinicie la inetrfaz
        onDispose {
            recorder.stop()
            player.release()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text("Audio - Reproductor")

        Text("Archivo: ${newAudioFile?.name ?: "Ninguno"} (${if (newAudioFile?.exists() == true) "existe" else "no existe"})")
        Text("Estado: {$status}")
        Text(if (audioGranted) "Permiso de microfono concedido" else "Permiso de microfono no concedido")

        if(!audioGranted){
            Button(onClick = {requestAudio()}) {
                Text("Solicitar microfono")
            }
        }
        else{
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = {
                        player.stop()
                        /*recorder.start(newAudioFile){status = it}
                        status = "Grabando"
                        val file = AppFiles.newAudioFile(context)
                        Toast.makeText(context, "Audio grabado en: \n${newAudioFile.absolutePath}", Toast.LENGTH_LONG).show()
                        */
                        val archivoNuevo = AppFiles.newAudioFile(context)
                        newAudioFile = archivoNuevo
                        recorder.start(archivoNuevo) {status = it}
                        status = "Grabando"
                    },
                    enabled = !recorder.isRecording()
                ) { Text("Grabar audio") }

                Button(onClick = {
                    recorder.stop()
                    status = "Grabación guardada"

                    newAudioFile?.let { archivo ->
                        val nuevaNota = AudioNota(
                            id = UUID.randomUUID().toString(),
                            nombre = "Audio ${listaAudios.size + 1}",
                            ruta = archivo.absolutePath,
                            fecha = System.currentTimeMillis()
                        )
                        listaAudios.add(nuevaNota)
                    }
                },
                    enabled = recorder.isRecording()
                ) { Text("Stop grabación") }
            }

            Spacer(Modifier.height(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ){
                Button(onClick = {
                    newAudioFile?.let { archivo ->
                        player.prepareFromFile(
                            file = archivo,
                            onCompleted = { status = "Terminado" },
                            onError = { msg -> status = msg }
                        )
                        status = "Listo para reproducir"
                    } ?: run { status = "Error: Graba algo primero" }

                }){ Text("Preparar")}

                Button(onClick = {
                    player.play { status = it }
                    if (status == "Listo para reproducir") status = "Reproduciondo"
                },
                    enabled = status == "Listo para reproducir") { Text("Play") }

                Button(onClick = {
                    player.pause()
                    status = "Pausado"
                }) { Text("Pause") }

                Button(onClick = {
                    player.stop()
                    status = "Parado"
                }) { Text("Stop") }
            }
        }

        Button(onClick = {navController.navigate(Routes.GRABACIONES)}) { Text("Ver audios") }


        Button(
            onClick = {navController.popBackStack()})
        { Text("Volver para atrás") }
    }
}