package com.example.stocker2

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.stocker2.databinding.LayoutRegistroBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query


private lateinit var binding: LayoutRegistroBinding
class ActividadRegistro: AppCompatActivity() {
    private lateinit var btn_atras: ImageView
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("supermercados")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        binding.btnRegReg.setOnClickListener{
            btguardarRegistro()

        }
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras=findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener{
            finish()
        }
    }
    private fun crearObjetosDelXml(){
        binding=LayoutRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    fun btguardarRegistro() {
        val nombreEmpresa = binding.ETNomEmpr.text.toString()
        val contrasena = binding.ETID.text.toString()

        if (nombreEmpresa.isEmpty() || contrasena.isEmpty()) {
            resultadoOperacion("El nombre de la empresa y la contraseña no pueden estar vacíos")
            return
        }

        // Consulta para obtener el último documento (ordenado por id de forma descendente)
        myCollection
            .orderBy("id", Query.Direction.DESCENDING)
            .limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                var nuevoId = 1 // Valor predeterminado si no hay supermercados

                if (!querySnapshot.isEmpty) {
                    val ultimoSupermercado = querySnapshot.documents[0]
                    val ultimoId = ultimoSupermercado.getLong("id")
                    if (ultimoId != null) {
                        nuevoId = (ultimoId + 1).toInt()
                    }
                }
                // Datos del nuevo supermercado, incluyendo el nuevo id
                val data = hashMapOf(
                    "id" to nuevoId,
                    "Contraseña" to contrasena,
                    "Ciudad" to binding.ETCiuEmpr.text.toString(),
                    "correo" to binding.ETTLF.text.toString(),
                    "paginaweb" to binding.ETPWBEmp.text.toString()
                )

                // Guarda el nuevo supermercado en la colección existente
                myCollection
                    .document(nombreEmpresa)
                    .set(data)
                    .addOnSuccessListener {
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
        binding.ETPWBEmp.setText("")
        binding.ETTLF.setText("")
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }



}