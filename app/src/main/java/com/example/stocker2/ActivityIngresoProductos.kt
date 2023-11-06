package com.example.stocker2

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import com.example.stocker2.databinding.ActivityIngresoProductosBinding
import com.example.stocker2.databinding.LayoutRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Objects
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var binding: ActivityIngresoProductosBinding
private lateinit var btn_atras:ImageView
class ActivityIngresoProductos : AppCompatActivity() {
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("Productos")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       crearObjetosDelXml()
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        val objIntent: Intent =intent
        var NombreEmpresa=objIntent.getStringExtra("NombreEmpresa")

        binding.btnGuardarProducto.setOnClickListener{

            if (NombreEmpresa != null) {
                guardarRegistro(NombreEmpresa)
                GlobalScope.launch {
                    delay(1000)
                    if (NombreEmpresa != null) {
                        listarDocumento(NombreEmpresa)
                    }
                }
            }
        }
        if (NombreEmpresa != null) {
            listarDocumento(NombreEmpresa)
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
        var TLF=objIntent.getStringExtra("Telefono")
        return when (item.itemId) {
                R.id.Web->{
                    if (PagWeb != null) {
                        abrirPagina(PagWeb)
                    }
                    true
                }
            R.id.Contactartlfn->{
                if (TLF != null) {
                    llamarTelefono(TLF)
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun guardarRegistro(NombreEmpresa:String){



        if (binding.etNombProd.text.toString().isEmpty() || binding.etCantProd.text.toString().isEmpty()) {
            resultadoOperacion("El nombre del produco y la cantidad no pueden estar vacíos")
            if (NombreEmpresa != null) {
                listarDocumento(NombreEmpresa)
            }
            return
        }

           myCollection.document(NombreEmpresa).get()
            .addOnSuccessListener {
                    if (it.contains(binding.etNombProd.text.toString())){
                        myCollection.document(NombreEmpresa).set(
                            hashMapOf(
                                binding.etNombProd.text.toString() to (binding.etCantProd.text.toString().toInt()+it.get(binding.etNombProd.text.toString()).toString().toInt())
                            ), SetOptions.merge())
                            .addOnSuccessListener {
                                resultadoOperacion("Se ha añadido la cantidad Correspondiente")

                            }
                            .addOnFailureListener{
                                    e->
                                Log.e("Firebase Update Error",e.message,e)
                            }
                    }
                    else{
                        myCollection.document(NombreEmpresa).set(
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
    fun llamarTelefono(TLF:String) {

        startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(TLF)))
    }

}