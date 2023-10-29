package com.example.stocker2

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.LayoutRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore


private lateinit var binding: LayoutRegistroBinding
class ActividadRegistro: AppCompatActivity() {

    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("supermercados")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        binding.btnRegReg.setOnClickListener{
            btguardarRegistro()

        }
    }
    private fun crearObjetosDelXml(){
        binding=LayoutRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun btguardarRegistro(){
        myCollection.document(binding.ETNomEmpr.text.toString()).get()
            .addOnSuccessListener {
                if (it.exists()){
                    resultadoOperacion("Este Supermercado ya esta registado")

                }
                else{
                    myCollection.document(binding.ETNomEmpr.text.toString()).set(
                        hashMapOf(
                            "Contraseña:" to binding.ETID.text.toString(),
                            "Ciudad:" to binding.ETCiuEmpr.text.toString())


                    )
                    resultadoOperacion("Registro guardado correctamente")
                    finish()
                }


            }



    }

    fun volver (view: View){

        finish()
    }
    private fun resultadoOperacion(mensaje:String){
        binding.ETID.setText("")
        binding.ETCiuEmpr.setText("")
        binding.ETNomEmpr.setText("")
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }



}