package com.example.tp2.interfaces


import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.tp2.interfaces.rememberPermissionState
import com.example.tp2.storage.AppFiles

@Composable

fun PermissionDemoScreen(){
    val context = LocalContext.current

    //Estado de permisos
    val (audioGranted, requestAudio) = rememberAudioPermission()
    val (cameraGranted, requestCamera) = rememberCameraPermission()

    Column(
        modifier = Modifier.
        fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Permiso de Audio")
        Text(if (audioGranted) "Concedido" else "No concedido")
        Button(onClick = {requestAudio()}) {
            Text("Solicitar microfono")
        }

        Button(
            onClick = {
                val file = AppFiles.audioFile(context)
                Toast.makeText(context, "Audio grabado en: \n${file.absolutePath}", Toast.LENGTH_LONG).show()
            },
            enabled = audioGranted
        ) {Text("Graba audio (demo)")}

        Divider()

        Text("Permiso de Camara")
        Text(if (cameraGranted) "Concedido" else "No concedido")

        Button(onClick = {requestCamera()}) {
            Text("Solicitar camara")
        }

        Button(
            onClick = {
                val file = AppFiles.newPhotoFile(context)
                Toast.makeText(context, "Foto guardada en: \n${file.absolutePath}", Toast.LENGTH_LONG).show()
            },
            enabled = cameraGranted
        ) {Text("Echar foto")}
    }
}