package com.example.tp2.interfaces

import android.util.Log
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.camera.core.Preview
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.tp2.storage.AppFiles
import java.io.File

@Composable
fun CameraScreen(navController: NavHostController){
    val context = LocalContext.current

    val lifecycleOwner = LocalLifecycleOwner.current

    val (hasCamPerm, requestCamPerm) = rememberCameraPermission()

    var status by remember { mutableStateOf("Listo") }

    var lastFileName by remember { mutableStateOf("Ninguna") }

    var imageCapture by remember { mutableStateOf<ImageCapture?>(null)}

    Log.d("CameraDebug", "Permiso cámara: $hasCamPerm")

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Camara")

        if(!hasCamPerm){
            Button(onClick = {requestCamPerm()}) {
                Text("Solicitar cámara")
            }
            return@Column
        }

        Text("Permiso cámara: ${if (hasCamPerm) "OK" else "NO"}")
        Text("Estado: ${status}")
        Text("Última foto: $lastFileName")


        //preview de la camara
        AndroidView(
            modifier = Modifier.fillMaxWidth().weight(1f),
            factory = {
                    ctx ->
                Log.d("Camera Debug", "Inicializando camara")

                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    val preview = Preview.Builder().build().also{
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }

                    val capture = ImageCapture.Builder()
                        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .build()

                    imageCapture = capture

                    try {
                        cameraProvider.unbindAll()

                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            capture
                        )

                        status = "Preview OK"
                        Log.d("CameraDebug", "Camara enlazada correctamente")
                    } catch (e: Exception){
                        status = "Error camara: ${e.message}"
                        Log.e("CameraDebug", "Error al hacer bind", e)
                    }
                }, ContextCompat.getMainExecutor((ctx)))

                previewView
            }
        )

        Button(onClick = {
            val capture = imageCapture
            if (capture == null){
                status = "Image capture no listo"
                return@Button
            }

            val file: File = AppFiles.latestPhotoFile(context)

            val options = ImageCapture.OutputFileOptions.Builder(file).build()

            status = "Capturado"

            capture.takePicture(
                options,
                ContextCompat.getMainExecutor(context),
                //resiltado de la foto
                object : ImageCapture.OnImageSavedCallback {
                    //exito
                    override fun onImageSaved(outputFileResult: ImageCapture.OutputFileResults) {
                        status = "Foto guardada"
                        lastFileName = file.name
                    }

                    override fun onError(exception: ImageCaptureException) {
                        status = "Error capturando: ${exception.message}"
                    }

                }
            )
        }){ Text("Hacer foto") }

        Button(onClick = {navController.navigate(Routes.IMAGE)}) { Text("Ver foto") }

        Button(
            onClick = {navController.popBackStack()})
        { Text("Volver para atrás") }
    }
}