package com.example.stocker2

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.LayoutInicioSesionBinding
import com.google.firebase.firestore.FirebaseFirestore

class ActividadInicioSesion: AppCompatActivity() {
    public lateinit var binding: LayoutInicioSesionBinding
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("supermercados")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        binding.BTNABREREG.visibility=View.INVISIBLE
        binding.BTNABREREG.isEnabled=false
        binding.btnIniSec.setOnClickListener{
            btInicioSesion()


        }

    }
    private fun crearObjetosDelXml(){
        binding=LayoutInicioSesionBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
    fun btInicioSesion(){
    myCollection.document(binding.ETISN.text.toString()).get()
        .addOnSuccessListener {
            if(it.exists()){



            }
            else{
                resultadoOperacion("Esta empresa no está registrada, Quieres Registrarte?")
                binding.BTNABREREG.visibility=View.VISIBLE
                binding.BTNABREREG.isEnabled=true
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


    fun volver (view: View){

        finish()
    }
}