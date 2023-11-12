package com.example.stocker2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.ActivityMainBinding

/**
 * Actividad principal de la aplicación Stocker2.
 */
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var btn_atras: ImageView

    /**
     * Se llama cuando la actividad está iniciando.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflar el diseño utilizando View Binding
        crearObjetosDelXML()

        // Configurar la ActionBar
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Encontrar y configurar el botón de retroceso
        btn_atras = findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener {
            finish()
        }
    }

    /**
     * Infla el diseño utilizando View Binding.
     */
    private fun crearObjetosDelXML() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Crea el menú de opciones en la ActionBar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main3, menu)
        return true
    }

    /**
     * Maneja la selección de elementos del menú.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.AcDe -> {
                // Abrir la actividad 'AcercaDeActivity'
                val intent = Intent(this, AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.Preferencias -> {
                // Abrir la actividad 'PREFERENCIAS'
                val intent = Intent(this, PREFERENCIAS::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Maneja el evento de clic para el botón 'Registrar'.
     */
    fun abrirRegistrar(view: View) {
        val intent = Intent(this, ActividadRegistro::class.java)
        startActivity(intent)
    }

    /**
     * Maneja el evento de clic para el botón 'Buscar Producto'.
     */
    fun abrirBuscarProducto(view: View) {
        val intent = Intent(this, BuscarProducto::class.java)
        startActivity(intent)
    }

    /**
     * Maneja el evento de clic para el botón 'Inicio Sesión'.
     */
    fun abrirInicioSec(view: View) {
        val intent = Intent(this, ActividadInicioSesion::class.java)
        startActivity(intent)
    }

    /**
     * Maneja el evento de clic para el botón 'Buscar Tienda'.
     */
    fun abrirBuscarTienda(view: View) {
        val intent = Intent(this, BuscarTienda::class.java)
        startActivity(intent)
    }
}
