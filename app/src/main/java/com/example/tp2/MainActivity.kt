package com.example.tp2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation.compose.rememberNavController
import com.example.tp2.interfaces.AppNav
import com.example.tp2.model.AudioNota
import com.example.tp2.ui.theme.TP2Theme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.tp2.storage.AppFiles

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TP2Theme {
                Surface{
                    val navController = rememberNavController()

                    val listaAudios = remember { mutableStateListOf<AudioNota>() }

                    val context = LocalContext.current

                    //para cargar las notas que estan en la memoria inetrna
                    LaunchedEffect(Unit) {
                        val archivoAnteriores = AppFiles.listAudioFiles(context)

                        archivoAnteriores.forEach { archivo ->
                            val notaExistente = AudioNota(
                                id = archivo.name,
                                nombre = archivo.name,
                                ruta = archivo.absolutePath,
                                fecha = archivo.lastModified()
                            )
                            listaAudios.add(notaExistente)
                        }
                    }

                    AppNav(navController, listaAudios)
                }
            }
        }
    }
}