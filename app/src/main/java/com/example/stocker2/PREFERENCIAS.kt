package com.example.stocker2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.stocker2.databinding.SettingsActivityBinding
import com.example.stocker2.databinding.ActivityProductosMercadoBinding

class PREFERENCIAS : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, PrefConfig())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}