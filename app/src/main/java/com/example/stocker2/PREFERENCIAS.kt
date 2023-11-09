package com.example.stocker2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.stocker2.databinding.ActivityPreferenciasBinding
import com.example.stocker2.databinding.ActivityProductosMercadoBinding

class PREFERENCIAS : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preferencias)
        if (savedInstanceState == null) {
            supportFragmentManager                              // Llamamos al manejador de fragmentos
                .beginTransaction()                             // Tenemos que pasarle el FrameLayout (SE LLAMA SETTINGS) en el que cargar el layout y
                .replace(R.id.settings, PrefConfig())           // la CLASE que realiza esa carga (SettingsFragment)
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }


}