package com.example.stocker2

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.example.stocker2.databinding.ActivityBuscarProductoBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

/**
 * [BuscarProducto] es una actividad que permite a los usuarios buscar productos en la base de datos,
 * filtrando por nombre de producto y mostrando los resultados en la interfaz de usuario.
 */
class BuscarProducto : AppCompatActivity() {

    private lateinit var btn_atras: ImageView
    private val db = FirebaseFirestore.getInstance()
    private val myCollectionp = db.collection("Productos")
    private val myCollections = db.collection("supermercados")
    private lateinit var binding: ActivityBuscarProductoBinding

    /**
     * Método llamado cuando se crea la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialización de los elementos del diseño XML
        crearObjetosDelXML()
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configuración del botón de retroceso
        btn_atras = findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener {
            finish()
        }

        // Configuración del botón de búsqueda
        binding.btFiltrar.setOnClickListener {
            realizarBusqueda()
        }
    }

    /**
     * Método que realiza la búsqueda de productos en la base de datos y muestra los resultados en la interfaz de usuario.
     */
    private fun realizarBusqueda() {
        val filtro = binding.textfiltro.text.toString().lowercase(Locale.ROOT)

        myCollectionp.get().addOnSuccessListener { resultados ->
            val linearLayoutContainer = findViewById<LinearLayout>(R.id.linearLayoutContaine)
            linearLayoutContainer.removeAllViews()

            for (resultado in resultados) {
                val data = resultado.data
                for (entry in data.orEmpty()) {
                    val key = entry.key
                    val value = entry.value.toString()

                    if (filtro.isEmpty() || key.lowercase(Locale.ROOT) == filtro) {
                        myCollections.document(resultado.id).get().addOnSuccessListener { result ->
                            val nombreS = result.getString("nombre")
                            val direccionS = result.getString("direccion")
                            val resources = resources
                            val configuration = resources.configuration
                            val languageCode = configuration.locales.get(0).language
                            val textView = TextView(this)
                            if (languageCode == "es") {
                                // El idioma del dispositivo es español
                                textView.text = " El $nombreS de $direccionS tiene $value unidades de $key:"
                            } else {
                                // El idioma del dispositivo no es español, mostrar en inglés o el idioma predeterminado deseado
                                textView.text = " The $nombreS at $direccionS has $value units of $key:"
                            }

                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            textView.setTextColor(Color.parseColor("#B68D8D"))
                            linearLayoutContainer.addView(textView)
                        }
                    }
                }
            }
        }
    }

    /**
     * Método llamado cuando se hace clic en un elemento del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.AcDe -> {
                val intent = Intent(this, AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Método llamado para crear el menú de opciones en la barra de acción.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

    /**
     * Método que inicializa la vinculación con los elementos de diseño XML.
     */
    private fun crearObjetosDelXML() {
        binding = ActivityBuscarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}
