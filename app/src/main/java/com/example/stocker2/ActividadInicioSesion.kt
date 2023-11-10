package com.example.stocker2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
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
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var binding: LayoutInicioSesionBinding
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("supermercados")
    private lateinit var btn_atras: ImageView

    var bolguardo=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        val sharedPreferences=getSharedPreferences(packageName+"_preferences", Context.MODE_PRIVATE)
        val siono=sharedPreferences.getString("boolgr","no")

            if(siono=="si"){
                bolguardo=true
            }

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

        if (bolguardo){
            binding.ETISN.setText(sharedPreferences.getString("nombrealmacenado",""))
            binding.etISC.setText(sharedPreferences.getString("contrasenaalmacenada",""))
            binding.etOpcDir.setText(sharedPreferences.getString("direccionalmacenada",""))
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
        val direccion: String = binding.etOpcDir.text.toString()

        if (nombreEmpresa.isEmpty() || contrasena.isEmpty()) {
            resultadoOperacion("Ni el nombre de la empresa ni la contraseña pueden estar vacíos")
            return
        }

        if (direccion.isEmpty()) {
            resultadoOperacion("Necesitas ingresar una dirección para buscar la empresa")
            return
        }



        verificarEmpresa(nombreEmpresa, contrasena, direccion)
    }

    private fun verificarEmpresa(nombreEmpresa: String, contrasena: String, direccion: String) {
        myCollection
            .whereEqualTo("nombre", nombreEmpresa)
            .whereEqualTo("Contraseña", contrasena)
            .whereEqualTo("direccion", direccion)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    val documento = querySnapshot.documents[0]
                    val idAbuscar = documento["id"].toString()
                    val editor = sharedPreferences.edit()
                    if(bolguardo){
                        editor.putString("nombrealmacenado",nombreEmpresa)
                        editor.putString("contrasenaalmacenada",contrasena)
                        editor.putString("direccionalmacenada",direccion)
                    }


                    obtenerDatosEmpresa(idAbuscar)
                } else {
                    resultadoOperacion("No se encontró ninguna empresa que coincida con los datos proporcionados.")
                }
            }
    }

    private fun obtenerDatosEmpresa(idEmpresa: String) {
        myCollection.document(idEmpresa).get()
            .addOnSuccessListener {
                val pagWeb: String = it.get("paginaweb").toString()
                val correo: String = it.get("correo").toString()
                val id: String = it.get("id").toString()

                resultadoOperacion("Bienvenido!")
                val intent = Intent(this, ActivityIngresoProductos::class.java)
                intent.putExtra("id", id)
                intent.putExtra("PaginaWeb", pagWeb)
                intent.putExtra("correo", correo)
                startActivity(intent)
            }
    }

    private fun resultadoOperacion(mensaje:String){
        /*
        binding.ETISN.setText("")
        binding.etISC.setText("")
*/
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
