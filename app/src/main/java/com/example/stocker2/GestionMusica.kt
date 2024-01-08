package com.example.stocker2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stocker2.databinding.ActivityBuscarTiendaBinding
import com.example.stocker2.databinding.ActivityGestionMusicaBinding

class GestionMusica : AppCompatActivity() {
    private lateinit var binding:ActivityGestionMusicaBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
    }
    private fun crearObjetosDelXml() {
        binding = ActivityGestionMusicaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}