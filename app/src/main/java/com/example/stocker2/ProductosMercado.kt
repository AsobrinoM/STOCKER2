package com.example.stocker2

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.ActivityProductosMercadoBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Actividad que muestra los productos de un supermercado y permite acciones como abrir su página web o enviar un correo electrónico.
 */
class ProductosMercado : AppCompatActivity() {
    private lateinit var binding: ActivityProductosMercadoBinding
    private lateinit var btn_atras: ImageView
    private val db = FirebaseFirestore.getInstance()
    private val productos = db.collection("Productos")
    private val supermercados = db.collection("supermercados")

    override fun onCreate(savedInstanceState: Bundle?) {
        // Obtener datos del Intent
        val objIntent: Intent = intent
        var NombreEmpresa: String? = null
        var id: String? = null
        if (objIntent.hasExtra("NombreEmpresa")) {
            NombreEmpresa = objIntent.getStringExtra("NombreEmpresa")
        }
        if (objIntent.hasExtra("id")) {
            id = objIntent.getStringExtra("id")
        }

        super.onCreate(savedInstanceState)
        // Inflar el diseño utilizando View Binding
        crearObjetosDelXML()

        // Configurar la ActionBar
        binding.textViewNombMerc.text = NombreEmpresa
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configurar el botón de retroceso
        btn_atras = findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener {
            finish()
        }

        // Listar los documentos (productos) del supermercado
        if (id != null) {
            listarDocumento(id)
        }
    }

    /**
     * Crea el menú de opciones en la ActionBar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act1, menu)
        return true
    }

    /**
     * Maneja la selección de elementos del menú.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val objIntent: Intent = intent
        val id = objIntent.getStringExtra("id")

        return when (item.itemId) {
            R.id.Web -> {
                // Abrir la página web del supermercado
                if (id != null) {
                    supermercados
                        .document(id)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val pagWeb = snapshot.getString("paginaweb")
                            if (!pagWeb.isNullOrBlank()) {
                                abrirPagina(pagWeb)
                            } else {
                                Toast.makeText(this, "El supermercado no tiene página web", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                true
            }
            R.id.Contactaremail -> {
                // Enviar un correo electrónico al supermercado
                if (id != null) {
                    supermercados
                        .document(id)
                        .get()
                        .addOnSuccessListener { snapshot ->
                            val email = snapshot.getString("correo")
                            if (!email.isNullOrBlank()) {
                                mandarEmail(email)
                            } else {
                                Toast.makeText(this, "El supermercado no tiene correo electrónico", Toast.LENGTH_LONG).show()
                            }
                        }
                }
                true
            }
            R.id.AcDe -> {
                // Abrir la actividad 'AcercaDeActivity'
                val intent = Intent(this, AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Abre la página web del supermercado.
     */
    private fun abrirPagina(pagWeb: String) {
        if (pagWeb.isEmpty() || pagWeb.isBlank()) {
            Toast.makeText(this, "El supermercado no tiene ninguna página web", Toast.LENGTH_LONG).show()
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pagWeb)))
        }
    }

    /**
     * Envia un correo electrónico al supermercado.
     */
    private fun mandarEmail(email: String) {
        if (email.isEmpty() || email.isBlank()) {
            Toast.makeText(this, "El supermercado no tiene una dirección de correo electrónico", Toast.LENGTH_LONG).show()
        } else {
            val intent = Intent(Intent.ACTION_SENDTO)
            intent.data = Uri.parse("mailto:$email")
            intent.putExtra(Intent.EXTRA_SUBJECT, "Contacto a empresa")
            intent.putExtra(Intent.EXTRA_TEXT, "")

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No se encontró una aplicación de correo electrónico.", Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Infla el diseño utilizando View Binding.
     */
    private fun crearObjetosDelXML() {
        binding = ActivityProductosMercadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Lista los documentos (productos) del supermercado.
     */
    private fun listarDocumento(id: String) {
        productos
            .document(id)
            .get()
            .addOnSuccessListener { resultado ->
                val data = resultado.data
                val linearLayoutContainer = findViewById<LinearLayout>(R.id.linearLayoutContainer)
                linearLayoutContainer.removeAllViews() // Limpiar vistas anteriores
                for (entry in data.orEmpty()) {
                    val key = entry.key
                    val value = entry.value.toString()
                    val textView = TextView(this)
                    textView.text = "$key: $value"
                    textView.setTextColor(Color.parseColor("#B68D8D"))
                    linearLayoutContainer.addView(textView)
                }
            }
    }
}