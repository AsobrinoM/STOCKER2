package com.example.stocker2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.stocker2.databinding.ActivityIngresoProductosBinding
import com.example.stocker2.databinding.LayoutRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import java.util.Objects

private lateinit var binding: ActivityIngresoProductosBinding
class ActivityIngresoProductos : AppCompatActivity() {
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("Productos")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       crearObjetosDelXml()
        val objIntent: Intent =intent
        var datosEnviados=objIntent.getStringExtra("NombreEmpresa")
        binding.btnGuardarProducto.setOnClickListener{

            if (datosEnviados != null) {
                guardarRegistro(datosEnviados)
            }

        }
    }
    private fun crearObjetosDelXml(){
        binding=ActivityIngresoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun guardarRegistro(NombreEmpresa:String){
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
    }}
    private fun resultadoOperacion(mensaje:String){
        binding.etCantProd.setText("")
        binding.etNombProd.setText("")
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }

    fun volver (view: View){

        finish()
    }
}