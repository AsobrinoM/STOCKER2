package com.example.stocker2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Email
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
                .document(NombreEmpresa)
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
                    } }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val objIntent: Intent = intent
        val NombreEmpresa = objIntent.getStringExtra("NombreEmpresa")

        return when (item.itemId) {
            R.id.Web -> {
                if (NombreEmpresa != null) {
                    supermercados
                        .document(NombreEmpresa)
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
                if (NombreEmpresa != null) {
                    supermercados
                        .document(NombreEmpresa)
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
            else -> super.onOptionsItemSelected(item)
        }
    }


    fun abrirPagina(PagWeb:String) {
        // Para ésta, el ACTION_VIEW va a buscar una página que abrir
        if (PagWeb.isEmpty()||PagWeb.isBlank()){
            Toast.makeText(this,"EL supermercado no tiene ninguna página web", Toast.LENGTH_LONG).show()
        }
        else{
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PagWeb)))
        }
    }
    fun mandarEmail(email: String) {
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


    private fun crearObjetosDelXML(){
        binding=ActivityProductosMercadoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}