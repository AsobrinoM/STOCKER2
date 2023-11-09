package com.example.stocker2

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toolbar
import androidx.preference.PreferenceFragmentCompat

class PrefConfig : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        // Inflar preferencias
        setPreferencesFromResource(R.xml.preferencias, rootKey)

        // Configurar la barra de herramientas
        val toolbar = requireActivity().findViewById<Toolbar>(R.id.toolb)
        val btnAtras = toolbar.findViewById<ImageView>(R.id.btn_atras)

        btnAtras.setOnClickListener {
            // Acción cuando se hace clic en el botón de retroceso
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}
