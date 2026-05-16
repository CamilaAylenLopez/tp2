package com.example.tp2.interfaces

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.tp2.model.AudioNota
import com.example.tp2.interfaces.HomeScreen

object Routes{
    const val HOME = "home"
    const val AUDIO = "audio"
    const val CAMERA = "camera"
    const val IMAGE = "image"
    const val VIDEO = "video"
    const val GRABACIONES = "gabraciones"
}

@Composable
// esta es la manera de definir un panel de navegación dentro de la app
fun AppNav(navController: NavHostController, listaAudios: MutableList<AudioNota>)
{
    //definir cual es la pantalla principal
    NavHost(navController = navController, startDestination = Routes.HOME)
    {
        composable(Routes.HOME) {HomeScreen(navController)}
        composable(Routes.AUDIO) {AudioScreen(navController, listaAudios)}
        composable(Routes.IMAGE) {ImageScreen(navController)}
        composable(Routes.VIDEO) {VideoScreen(navController)}
        composable(Routes.CAMERA) {CameraScreen(navController)}
        composable(Routes.GRABACIONES) {GrabacionesScreen(navController, listaAudios)}
    }
}