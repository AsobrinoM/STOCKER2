package com.example.stocker2

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.LayoutInicioSesionBinding

class ActividadInicioSesion: AppCompatActivity() {
    public lateinit var binding: LayoutInicioSesionBinding



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

    }
    private fun crearObjetosDelXml(){
        binding=LayoutInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


    fun volver (view: View){

        finish()
    }
}