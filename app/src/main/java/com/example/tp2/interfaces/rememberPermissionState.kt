package com.example.tp2.interfaces

import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import android.Manifest
import androidx.core.content.ContextCompat

@Composable
fun rememberPermissionState(permission: String) : Pair<Boolean, () -> Unit>{
    val context = LocalContext.current

    // guarda si tiene el permiso, con el mutableStateOf se actualiza solo
    var granted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
        )
    }

    //muestra el cartelito de "permite acceder a..." y actualiza según la respuesta
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted -> granted = isGranted }

    val request = {launcher.launch(permission)}

    return Pair(granted, request)
}

@Composable
fun rememberAudioPermission() =
    rememberPermissionState(android.Manifest.permission.RECORD_AUDIO)

@Composable
fun rememberCameraPermission() =
    rememberPermissionState(android.Manifest.permission.CAMERA)