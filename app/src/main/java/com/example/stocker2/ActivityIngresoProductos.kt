package com.example.stocker2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.stocker2.databinding.ActivityIngresoProductosBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Objects
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class ActivityIngresoProductos : AppCompatActivity() {
    private lateinit var binding: ActivityIngresoProductosBinding
    private lateinit var btn_atras:ImageView
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("Productos")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       crearObjetosDelXml()
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val objIntent: Intent =intent
        var id=objIntent.getStringExtra("id")

        binding.btnGuardarProducto.setOnClickListener{

            if (id != null) {
                guardarRegistro(id)
                GlobalScope.launch {
                    delay(1000)
                    if (id != null) {
                        listarDocumento(id)
                    }
                }
            }
        }
        if (id != null) {
            listarDocumento(id)
        }
        btn_atras=findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener{
            finish()
        }
    }



    private fun crearObjetosDelXml(){
        binding=ActivityIngresoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act1, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val objIntent: Intent =intent
        var PagWeb=objIntent.getStringExtra("PaginaWeb")
        var email=objIntent.getStringExtra("correo")
        return when (item.itemId) {
                R.id.Web->{
                    if (PagWeb != null) {
                        abrirPagina(PagWeb)
                    }
                    true
                }
            R.id.Contactaremail->{
                    if (email != null) {
                        mandarCorreo(email)
                    }

                true
            }
            R.id.AcDe ->{
                val intent= Intent(this,AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }


            else -> {super.onOptionsItemSelected(item)}
        }

        }



    private fun guardarRegistro(id:String){
        if (binding.etNombProd.text.toString().isEmpty() || binding.etCantProd.text.toString().isEmpty()) {
            resultadoOperacion("ni nombre del produco ni la cantidad pueden estar vacíos")
            listarDocumento(id)
            return
        }

           myCollection.document(id).get()
            .addOnSuccessListener {
                    if (it.contains(binding.etNombProd.text.toString())){
                        myCollection.document(id).set(
                            hashMapOf(
                                binding.etNombProd.text.toString() to (binding.etCantProd.text.toString().toInt()+it.get(binding.etNombProd.text.toString()).toString().toInt())
                            ), SetOptions.merge())
                            .addOnSuccessListener {
                                eliminarSieso(id,binding.etNombProd.text.toString())

                            }
                            .addOnFailureListener{
                                    e->
                                Log.e("Firebase Update Error",e.message,e)
                            }
                    }
                    else{
                        myCollection.document(id).set(
                            hashMapOf(
                                binding.etNombProd.text.toString() to (binding.etCantProd.text.toString().toInt())
                            ), SetOptions.merge())
                            .addOnSuccessListener {
                                resultadoOperacion("El producto se ha registrado correctamente")
                            }
                            .addOnFailureListener{
                                    e->
                                Log.e("Firebase Update Error",e.message,e)
                    }
                }

    }


    }
    private fun eliminarSieso(id: String, nombreProducto: String) {
        myCollection.document(id).get()
            .addOnSuccessListener { documentSnapshot ->
                val cantidadActual = documentSnapshot.getLong(nombreProducto)

                if (cantidadActual != null) {
                    if (cantidadActual <= 0) {
                        // El producto tiene una cantidad igual o menor a 0, por lo que se eliminará
                        val data = HashMap<String, Any>()
                        data[nombreProducto] = FieldValue.delete()

                        myCollection.document(id)
                            .update(data)
                            .addOnSuccessListener {
                                resultadoOperacion("El producto se ha eliminado debido a que su cantidad ha bajado a 0.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase Delete Error", e.message, e)
                            }
                    }
                    else{
                        resultadoOperacion("El producto se ha editado correctamente")
                    }
                }

            }
    }
    private fun listarDocumento(nombreDocumento: String) {
        myCollection
            .document(nombreDocumento)
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
    private fun resultadoOperacion(mensaje:String){
        binding.etCantProd.setText("")
        binding.etNombProd.setText("")
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()

    }
    fun abrirPagina(PagWeb:String) {
        // Para ésta, el ACTION_VIEW va a buscar una página que abrir
        if (PagWeb.isEmpty()||PagWeb.isBlank()){
            Toast.makeText(this,"No intoduciste ninguna página web al registrarte",Toast.LENGTH_LONG).show()
        }
        else{


        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PagWeb)))
    }}
    fun mandarCorreo(email:String) {

        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                type="text/plain"
                putExtra(Intent.EXTRA_SUBJECT,"Contacto a empresa")
                putExtra(Intent.EXTRA_TEXT,"")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))

            }
        )
    }

}