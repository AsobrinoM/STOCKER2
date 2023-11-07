package com.example.stocker2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.stocker2.databinding.ActivityProductosMercadoBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume


class ProductosMercado : AppCompatActivity() {
    private lateinit var binding:ActivityProductosMercadoBinding
    private lateinit var btn_atras: ImageView

    private val db= FirebaseFirestore.getInstance()
    private val productos=db.collection("Productos")
    private val supermercados=db.collection("supermercados")
    override fun onCreate(savedInstanceState: Bundle?) {

        val objIntent: Intent = intent
        var NombreEmpresa: String? = null

        if (objIntent.hasExtra("NombreEmpresa")) {
            NombreEmpresa = objIntent.getStringExtra("NombreEmpresa")
        }

        super.onCreate(savedInstanceState)
        crearObjetosDelXML()
        binding.textViewNombMerc.text=NombreEmpresa
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras=findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener{
            finish()
        }
        if (NombreEmpresa != null) {
            listarDocumento(NombreEmpresa)
        }



    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act1, menu)
        return true
    }
    private fun listarDocumento(NombreEmpresa:String) {
            productos
                .document(NombreEmpresa!!)
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
                        linearLayoutContainer.addView(textView)
                    }
                }


    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val objIntent: Intent = intent
        val NombreEmpresa = objIntent.getStringExtra("NombreEmpresa")
        var pagWeb = ""
        var tlf = ""

        if (NombreEmpresa != null) {
            supermercados
                .document(NombreEmpresa)
                .get()
                .addOnSuccessListener { snapshot ->
                    pagWeb = snapshot.get("paginaweb").toString()
                    tlf = snapshot.get("Telefono").toString()
                }
        }

        // Utiliza una función suspendida para esperar a que las operaciones asincrónicas se completen
        GlobalScope.launch {
            val webReady = waitForData { pagWeb.isNotEmpty() }
            val tlfReady = waitForData { tlf.isNotEmpty() }

            if (webReady && tlfReady) {
                when (item.itemId) {
                    R.id.Web -> {
                        abrirPagina(pagWeb)
                    }
                    R.id.Contactartlfn -> {
                        llamarTelefono(tlf)
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }
        }

        return true
    }

    private suspend fun waitForData(condition: () -> Boolean) = suspendCancellableCoroutine<Boolean> { cont ->
        val checkInterval = 100 // Intervalo de comprobación en milisegundos

        fun checkCondition() {
            if (condition()) {
                cont.resume(true)
            } else {
                GlobalScope.launch {
                    delay(checkInterval.toLong())
                    checkCondition()
                }
            }
        }

        checkCondition()
    }
    fun abrirPagina(PagWeb:String) {
        // Para ésta, el ACTION_VIEW va a buscar una página que abrir
        if (PagWeb.isEmpty()||PagWeb.isBlank()){
            Toast.makeText(this,"EL supermercado no tiene ninguna página web", Toast.LENGTH_LONG).show()
        }
        else{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PagWeb)))
        }}
    fun llamarTelefono(TLF:String) {
        if (TLF.isEmpty()||TLF.isBlank()){
            Toast.makeText(this,"EL supermercado no tiene teléfono al que contactar", Toast.LENGTH_LONG).show()
        }
        else{
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(TLF)))
        }

    }


    private fun crearObjetosDelXML(){
        binding=ActivityProductosMercadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}