package com.example.stocker2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.stocker2.databinding.ActivityIngresoProductosBinding
import com.example.stocker2.databinding.LayoutRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore

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

                guardarRegistro(datosEnviados)

        }
    }
    private fun crearObjetosDelXml(){
        binding=ActivityIngresoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun guardarRegistro(NombreEmpresa:String?){
        myCollection.document(binding.etNombProd.text.toString()).get()
            .addOnSuccessListener {
                if (it.exists()){
                    myCollection.document(binding.etNombProd.text.toString()).set(
                        hashMapOf(
                            "cantidad" to (binding.etCantProd.text.toString().toInt()+it.get("cantidad").toString().toInt()),
                            "Supermercado" to (NombreEmpresa).toString()
                        )
                    )
                    resultadoOperacion("Se ha añadido la cantidad correctamente")
                }
                else{
                    myCollection.document(binding.etNombProd.text.toString()).set(
                        hashMapOf(
                            "cantidad" to (binding.etCantProd.text.toString().toInt()),
                            "Supermercado" to (NombreEmpresa).toString()

                        )


                    )
                    resultadoOperacion("El producto se ha registrado correctamente")

                }
            }








    }
    private fun resultadoOperacion(mensaje:String){
        binding.etCantProd.setText("")
        binding.etNombProd.setText("")
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }

    fun volver (view: View){

        finish()
    }
}