package com.example.tp2.interfaces

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController

@Composable
fun HomeScreen(navController: NavHostController){

    Column(
        Modifier.fillMaxSize().padding(40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ){
        Text("Practica 2 - Programación Multimedia")
        Button(onClick = {navController.navigate(Routes.AUDIO)}) { Text("Grabar audio") }
        Button(onClick = {navController.navigate(Routes.GRABACIONES)}) { Text("Ver Grabaciones") }
        Button(onClick = {navController.navigate(Routes.CAMERA)}) { Text("Camara") }
        Button(onClick = {navController.navigate(Routes.IMAGE)}) { Text("Imagen") }
        Button(onClick = {navController.navigate(Routes.VIDEO)}) { Text("Video") }
    }
}