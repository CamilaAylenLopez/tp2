package com.example.tp2.interfaces

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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

    DisposableEffect(Unit) {
        onDispose { player.release() }
    }

    Column(
        Modifier.fillMaxSize().padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text("Grabaciones - Estado: ${status}")

        if (listaAudios.isEmpty()){
            Text("No hay ninguna grabación todavía")
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
                            audioSeleccionado = audio
                            val file = java.io.File(audio.ruta)
                            if (file.exists()){
                                player.prepareFromFile(
                                    file = file,
                                    onCompleted = {
                                        status = "Terminado"
                                    }, onError = { msg -> status = "Error: $msg" }
                                )
                                status = "Preparado: ${audio.nombre}"
                            }
                            else{
                                Toast.makeText(context, "Archivo no encontrado", Toast.LENGTH_SHORT).show()
                            }
                        }, modifier = Modifier.fillMaxWidth(), enabled = true
                    ){
                        Column(modifier = Modifier.padding(8.dp)) {
                            Text(audio.nombre)
                            Text("ruta: ${audio.ruta}")
                            Text("Elegir para reproducir", fontSize = 20.sp)
                        }
                    }
                }
            }
            if(audioSeleccionado != null){
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Audio: ${audioSeleccionado?.nombre}")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ){
                        Button(onClick = {
                            player.play { status = it }
                        }) { Text("Play") }

                        Button(onClick = {
                            player.pause()
                            status = "Pausado"
                        }) { Text("Pause") }

                        Button(onClick = {
                            player.stop()
                            status = "Parado"
                            audioSeleccionado = null
                        }) { Text("Stop") }
                    }
                }
            }

        }

        Button(onClick = {navController.navigate(Routes.AUDIO)}) { Text("Grabar audio") }

        Button(
            onClick = {navController.popBackStack()})
        { Text("Volver para atrás") }
    }
}