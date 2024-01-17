package com.example.stocker2

import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.SoundPool
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.ActivityProductosMercadoBinding
import com.example.stocker2.databinding.ActivityVerVideoBinding
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
    lateinit var soundPool: SoundPool
    var eslogan:Int=0
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
        playSlogan(NombreEmpresa!!)
        binding.btnEntVid?.setOnClickListener {
            if (id != null) {
                comprobarUrlVideoYIniciarActividad(id)
            }
        }
    }

    /**
     * Crea el menú de opciones en la ActionBar.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act1, menu)
        return true
    }
    private fun comprobarUrlVideoYIniciarActividad(id: String) {
        supermercados.document(id).get().addOnSuccessListener { documento ->
            if (documento.exists() && documento.contains("urlVideo")) {
                val intent = Intent(this, VerVideo::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lo siento, este supermercado no tiene video promocional", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            Log.e("Firestore", "Error al obtener datos", it)
            Toast.makeText(this, "Error al acceder a la información del supermercado", Toast.LENGTH_LONG).show()
        }
    }
    private fun playSlogan(nombre: String) {
        Log.d("tajete", "Ha entrado en Slogan , el nombre es $nombre")
        var audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .build()

        soundPool = SoundPool.Builder()
            .setMaxStreams(5)
            .setAudioAttributes(audioAttributes)
            .build()

        val soundID = when (nombre) {
            "Mercadona" -> R.raw.mercadona
            "Carrefour" -> R.raw.carrefour
            "Dia" -> R.raw.dia
            else -> null
        }

        soundID?.let {
            val eslogan = soundPool.load(this, it, 1)
            soundPool.setOnLoadCompleteListener { soundPool, sampleId, status ->
                if (status == 0 && sampleId == eslogan) {
                    soundPool.play(eslogan, 9F, 9F, 1, 0, 1F)
                }
            }
        }
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
                            Log.d("FirestoreData", "correo: $email")
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

            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(pagWeb)))
    }

    /**
     * Envia un correo electrónico al supermercado.
     */
    private fun mandarEmail(email: String) {
        Log.d("FirestoreData", "el correo ha entrado en la funcion $email")
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