package com.example.stocker2

import android.content.Intent
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
import com.example.stocker2.databinding.LayoutInicioSesionBinding
import com.example.stocker2.databinding.LayoutRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class BuscarProducto : AppCompatActivity() {


    private lateinit var btn_atras: ImageView
    private val db= FirebaseFirestore.getInstance()
    private val myCollectionp=db.collection("Productos")
    private val myCollections=db.collection("supermercados")
    lateinit var binding: ActivityBuscarProductoBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXML()
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras=findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener{
            finish()
        }
        binding.btFiltrar.setOnClickListener {
            realizarBusqueda()
        }
    }
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

                            val textView = TextView(this)
                            textView.text = " El $nombreS de $direccionS tiene $value unidades de $key:"
                            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            linearLayoutContainer.addView(textView)
                        }
                    }
                }
            }
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.AcDe ->{
                val intent= Intent(this,AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }


            else -> {super.onOptionsItemSelected(item)}
        }

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }




    private fun crearObjetosDelXML(){
        binding= ActivityBuscarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}