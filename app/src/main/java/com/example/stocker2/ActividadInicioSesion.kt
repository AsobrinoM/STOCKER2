package com.example.stocker2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.LayoutInicioSesionBinding
import com.google.firebase.firestore.FirebaseFirestore

class ActividadInicioSesion: AppCompatActivity() {
    lateinit var binding: LayoutInicioSesionBinding
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("supermercados")
    private lateinit var btn_atras: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        binding.BTNABREREG.visibility=View.INVISIBLE
        binding.BTNABREREG.isEnabled=false
        binding.btnIniSec.setOnClickListener {
            btInicioSesion()
        }
            setSupportActionBar(binding.appbar.toolb)
            supportActionBar?.setDisplayShowTitleEnabled(false)
            btn_atras=findViewById(R.id.btn_atras)
            btn_atras.setOnClickListener{
                finish()
            }


    }
    private fun crearObjetosDelXml(){
        binding=LayoutInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }
    fun btInicioSesion() {
        val nombreEmpresa: String = binding.ETISN.text.toString()
        val contrasena: String = binding.etISC.text.toString()

        if (nombreEmpresa.isEmpty() || contrasena.isEmpty()) {
            resultadoOperacion("El nombre de la empresa y la contraseña no pueden estar vacíos")
            return
        }

        myCollection
            .whereEqualTo("nombre", nombreEmpresa)
            .whereEqualTo("Contraseña", contrasena)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents[0]
                    val idAbuscar = documento["id"].toString()
                    if (idAbuscar != null) {
                        myCollection.document(idAbuscar).get()
                            .addOnSuccessListener {
                                val pagWeb:String=it.get("paginaweb").toString()
                                val correo:String=it.get("correo").toString()
                                val id:String=it.get("id").toString()
                                resultadoOperacion("Bienvenido!")
                                val intent= Intent(this,ActivityIngresoProductos::class.java)
                                intent.putExtra("id",id)
                                intent.putExtra("PaginaWeb",pagWeb)
                                intent.putExtra("correo",correo)
                                startActivity(intent)
                                                    }

                    } else {
                        resultadoOperacion("Error: ID no válido para esta empresa.")
                    }
                } else {
                    resultadoOperacion("Esta empresa no está registrada. ¿Quieres registrarte?")
                    binding.BTNABREREG.visibility = View.VISIBLE
                    binding.BTNABREREG.isEnabled = true
                }
            }
    }


    private fun resultadoOperacion(mensaje:String){
        binding.ETISN.setText("")
        binding.etISC.setText("")

        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }
    fun abrirRegistrar(view: View){
        val intent= Intent(this,ActividadRegistro::class.java)
        startActivity(intent)
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

}
/**
 *
 */