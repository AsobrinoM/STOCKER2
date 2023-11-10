package com.example.stocker2

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

        fun btguardarRegistro() {
            val nombreEmpresa = binding.ETNomEmpr.text.toString()
            val contrasena = binding.ETID.text.toString()
            val direccion=binding.ETDir.text.toString()
            if (nombreEmpresa.isEmpty() || contrasena.isEmpty()||direccion.isEmpty()) {
                resultadoOperacion("El nombre de la empresa, la contraseña y la direccion son obligatorios")
                return
            }

            // Realiza una consulta para buscar documentos con el mismo nombre y contraseña
            myCollection
                .whereEqualTo("nombre", nombreEmpresa)
                .whereEqualTo("direccion", direccion)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    if (!querySnapshot.isEmpty) {
                        // Ya existe un registro con el mismo nombre y direccion
                        resultadoOperacion("Este supermercado con esta dirección ya está registrado.")
                    } else {
                        myCollection
                            .orderBy("id", Query.Direction.DESCENDING)
                            .limit(1)
                            .get()
                            .addOnSuccessListener { querySnapshot ->
                                val nuevoId = if (!querySnapshot.isEmpty) {
                                    val ultimoSupermercado = querySnapshot.documents[0]
                                    val ultimoId = ultimoSupermercado.getLong("id")
                                    ultimoId?.toInt()?.plus(1) ?: 1
                                } else {
                                    1
                                }

                                // Datos del nuevo supermercado, incluyendo el nuevo ID
                                val data = hashMapOf(
                                    "id" to nuevoId,
                                    "nombre" to nombreEmpresa,
                                    "Contraseña" to contrasena,
                                    "Ciudad" to binding.ETCiuEmpr.text.toString(),
                                    "direccion" to direccion,
                                    "correo" to binding.ETTLF.text.toString(),
                                    "paginaweb" to binding.ETPWBEmp.text.toString()
                                )

                                // Guarda el nuevo supermercado en la colección existente
                                myCollection
                                    .document(nuevoId.toString())
                                    .set(data)
                                    .addOnSuccessListener {
                                        resultadoOperacion("Registro guardado correctamente")
                                        finish()
                                    }
                            }
                    }
                }
        }

        // Realiza una consulta para obtener todos los documentos y encontrar el último ID


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
    private fun resultadoOperacion(mensaje:String){
        binding.ETID.setText("")
        binding.ETCiuEmpr.setText("")
        binding.ETNomEmpr.setText("")
        binding.ETPWBEmp.setText("")
        binding.ETTLF.setText("")
        Toast.makeText(this,mensaje, Toast.LENGTH_LONG).show()
    }



}