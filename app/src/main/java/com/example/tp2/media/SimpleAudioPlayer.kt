package com.example.tp2.media

import android.media.MediaPlayer
import androidx.compose.runtime.DisposableEffectResult
import java.io.File

class SimpleAudioPlayer{

    private var mp: MediaPlayer ?= null

    fun prepareFromFile(file: File, onCompleted: () -> Unit, onError: (String) -> Unit){
        release()
        try {
            if (!file.exists()){
                onError("El archivo no existe: ${file.name}")
                return
            }

            mp = MediaPlayer().apply {
                setDataSource(file.absolutePath)
                setOnCompletionListener { onCompleted() }
                prepareAsync()
            }
        }catch (e: Exception){

        }
    }

    fun play(onError: (String) -> Unit){
        try {
            mp?.start() ?: onError("No esta preparado")
        }catch (e: Exception){
            onError("Error play: ${e.message}")
        }
    }

    fun pause(){
        try{
            if(mp?.isPlaying == true){
                mp?.pause()
            }
        }catch (e: Exception){ }
    }

    fun stop(){
        try {
            mp?.stop()
            mp?.prepareAsync()
        }catch (e: Exception){}finally {
            release()
        }
    }

    fun release(): Unit {
        mp?.release()
        mp = null
    }

}