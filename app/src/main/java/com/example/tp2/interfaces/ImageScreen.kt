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
import androidx.compose.ui.window.isPopupLayout
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

    var status by remember { mutableStateOf("Listo para procesar") }
    var isProcessing by remember {mutableStateOf(false)}

    var original by remember { mutableStateOf<Bitmap?>(null) }
    var processed by remember { mutableStateOf<Bitmap?>(null) }

    val toJpgProcessor = remember { ToJpgProcessor() }
    val toPngProcessor = remember { ToPngProcessor() }


    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Text("Imagen - Convertir PNG a JPG y viceversa")
        Text("Entrada: ${inputFile.name} (${if (fileExits) "Disponible" else "No se ha encontrado ninguna imagen, toma una foto primero"})")
        Text("Estado actual: ${status}")

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = {
                if (fileExits) {
                    original = ImageStorage.loadBitmap(inputFile)
                    status = "Imagen original cargada"
                    processed = null
                }else{
                    status = "Error: No se encontro el archivo"
                }
            },
                enabled = !isProcessing,
                modifier = Modifier.weight(1f)
            ) {Text("Cargar foto") }

            Button(onClick = {
                val src = original
                if (src != null){
                    status = "Convirtiendo y guardando en JPG..."

                    try {
                        val resultado = toJpgProcessor.apply(src)
                        processed = resultado

                        val archivoDestino = AppFiles.processedJpgFile(context)

                        val fos = FileOutputStream(archivoDestino)
                        resultado.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                        fos.close()

                        status = "Imagen procesada a JPG correctamente y guardada en: ${archivoDestino.name}"
                    }catch (e: Exception){
                        status = "Error al procesar/guardar JPG: ${e.message}"
                    }finally {
                        isProcessing = false
                    }

                } else{
                    status = "Primero debes cargar la imagen!"
                }
            },
                enabled = !isProcessing && original != null,
                modifier = Modifier.weight(1f)
            ) {Text("Convertir a JPG") }

            Button(onClick = {
                val src = original
                if (src != null){
                    isProcessing = true
                    status = "Convirtiendo y guardando en PNG..."

                    try {
                        val resultado = toPngProcessor.apply(src)
                        processed = resultado

                        val archivoDestino = AppFiles.processedPngFile(context)
                        val fos = FileOutputStream(archivoDestino)
                        resultado.compress(Bitmap.CompressFormat.PNG, 100, fos)
                        fos.close()

                        status = "Imagen procesada a PNG correctamente y guardada en: ${archivoDestino.name}"
                    }catch (e: Exception){
                        status = "Error al procesar/guardar JPG: ${e.message}"
                    }finally {
                        isProcessing = false
                    }

                } else{
                    status = "Primero debes cargar la imagen!"
                }
            },
                enabled = !isProcessing && original != null,
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
                        contentDescription = "Imagen original",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            processed?.let {
                Column(Modifier.weight(1f)) {
                    Text(("Imagen procesada"))
                    androidx.compose.foundation.Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "Imagen procesada",
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = {navController.popBackStack()},
            modifier = Modifier.fillMaxWidth()
        )
        { Text("Volver para atrás") }
    }
}