package com.example.tp2.interfaces

import android.graphics.Bitmap
import android.media.Image
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tp2.imageproc.ToJpgProcessor
import com.example.tp2.imageproc.ToPngProcessor
import com.example.tp2.storage.AppFiles
import com.example.tp2.storage.ImageStorage
import java.io.File
import java.io.FileOutputStream

@Composable
fun ImageScreen(navController: NavHostController){
    val context = LocalContext.current
    val inputFile = AppFiles.latestPhotoFile(context)
    var fileExits by remember { mutableStateOf(inputFile.exists()) }
    var status by remember { mutableStateOf("Listo") }
    var original by remember { mutableStateOf<Bitmap?>(null) }
    var processed by remember { mutableStateOf<Bitmap?>(null) }
    val toJpgProcessor = remember { ToJpgProcessor() }
    val toPngProcessor = remember { ToPngProcessor() }


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Imagen - Convertir PNG a JPG y viceversa")
        Text("Entrada: ${inputFile.name} (${if (fileExits) "existe" else "no existe, primero toma una foto"})")
        Text("Estado:${status}")

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(onClick = {
                if (fileExits) {
                    original = ImageStorage.loadBitmap(inputFile)
                    status = "Cargando"
                    processed = null
                }else{
                    status = "El archivo no existe"
                }
            },

            ) {Text("Cargar foto") }

            Button(onClick = {
                val src = original
                val file: File = AppFiles.processedJpgFile(context)
                if (src != null){
                    status = "Procesando a JPG..."
                    val resultado = toJpgProcessor.apply(src)
                    processed = resultado

                    val archivoDestino: File = AppFiles.processedJpgFile(context)

                    val fos = FileOutputStream(archivoDestino)
                    resultado.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos.close()

                    status = "Procesada a JPG  y guardada en: ${archivoDestino.name}"
                } else{
                    status = "Primero carga una imagen"
                }
            },
                modifier = Modifier.weight(1f)
            ) {Text("Convertir a JPG") }

            Button(onClick = {
                val src = original
                val file: File = AppFiles.processedJpgFile(context)
                if (src != null){
                    status = "Procesando a PNG..."
                    val resultado = toPngProcessor.apply(src)
                    processed = resultado

                    val archivoDestino: File = AppFiles.processedPngFile(context)

                    val fos = FileOutputStream(archivoDestino)
                    resultado.compress(Bitmap.CompressFormat.PNG, 100, fos)
                    fos.close()

                    status = "Procesada a PNG y guardada en: ${archivoDestino.name}"
                } else{
                    status = "Primero carga una imagen"
                }
            },
                modifier = Modifier.weight(1f)
            ) {Text("Convertir a PNG") }

        }

        Spacer(Modifier.height(8.dp))
        Text("Vista previa:")

        Row(Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            original?.let {
                Column(Modifier.weight(1f)) {
                    Text(("Original"))
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            processed?.let {
                Column(Modifier.weight(1f)) {
                    Text(("Imagen procesada"))
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {navController.popBackStack()})
        { Text("Volver para atrás") }
    }
}