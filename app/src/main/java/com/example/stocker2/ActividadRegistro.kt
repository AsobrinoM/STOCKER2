package com.example.stocker2

import android.content.Intent
import android.os.Bundle
import android.view.ContextMenu
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

/**
 * [ActividadRegistro] es una actividad que permite a los usuarios registrar nuevos supermercados.
 */
class ActividadRegistro : AppCompatActivity() {

    private lateinit var btn_atras: ImageView
    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("supermercados")

    private lateinit var binding: LayoutRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()

        binding.btnRegReg.setOnClickListener {
            btguardarRegistro()
        }

        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras = findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener {
            finish()
        }

        // Agregar OnLongClickListener para ETNomEmpr
        binding.ETNomEmpr.setOnLongClickListener {
            openContextMenu(binding.ETNomEmpr)
            true
        }

        // Agregar OnLongClickListener para ETCiuEmpr
        binding.ETCiuEmpr.setOnLongClickListener {
            openContextMenu(binding.ETCiuEmpr)
            true
        }

        registerForContextMenu(binding.ETNomEmpr)
        registerForContextMenu(binding.ETCiuEmpr)
    }



    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        when (v?.id) {
            R.id.ETNomEmpr -> menuInflater.inflate(R.menu.menu_contextual_sucursales, menu)
            R.id.ETCiuEmpr -> menuInflater.inflate(R.menu.menu_contextual_ciudades, menu)
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            // Opciones para ETNomEmpr
            R.id.option_mercadona -> binding.ETNomEmpr.setText("Mercadona")
            R.id.option_dia -> binding.ETNomEmpr.setText("Dia")
            R.id.option_lidl -> binding.ETNomEmpr.setText("Lidl")
            R.id.option_carrefour -> binding.ETNomEmpr.setText("Carrefour")
            R.id.option_eroski -> binding.ETNomEmpr.setText("Eroski")
            R.id.option_consum -> binding.ETNomEmpr.setText("Consum")

            // Opciones para ETCiuEmpr
            R.id.option_ciudad_real -> binding.ETCiuEmpr.setText("Ciudad Real")
            R.id.option_madrid -> binding.ETCiuEmpr.setText("Madrid")
            R.id.option_almagro -> binding.ETCiuEmpr.setText("Almagro")
            R.id.option_puertollano -> binding.ETCiuEmpr.setText("Puertollano")
            R.id.option_pozuelo -> binding.ETCiuEmpr.setText("Pozuelo")
            R.id.option_daimiel -> binding.ETCiuEmpr.setText("Daimiel")
            R.id.option_tomelloso -> binding.ETCiuEmpr.setText("Tomelloso")
            R.id.option_sevilla -> binding.ETCiuEmpr.setText("Sevilla")
            R.id.option_cordoba -> binding.ETCiuEmpr.setText("Cordoba")
            R.id.option_granada -> binding.ETCiuEmpr.setText("Granada")

            else -> return super.onContextItemSelected(item)
        }
        return true
    }

    private fun crearObjetosDelXml() {
        binding = LayoutRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    /**
     * Método llamado para crear el menú de opciones en la barra de acción.
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main2, menu)
        return true
    }

    /**
     * Método llamado cuando se hace clic en un elemento del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.AcDe -> {
                // Abrir la actividad AcercaDeActivity
                val intent = Intent(this, AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Método que guarda el nuevo registro de supermercado en Firebase Firestore.
     */
    fun btguardarRegistro() {
        val nombreEmpresa = binding.ETNomEmpr.text.toString()
        val contrasena = binding.ETID.text.toString()
        val direccion = binding.ETDir.text.toString()

        if (nombreEmpresa.isEmpty() || contrasena.isEmpty() || direccion.isEmpty()) {
            resultadoOperacion("El nombre de la empresa, la contraseña y la dirección son obligatorios")
            return
        }

        // Realiza una consulta para buscar documentos con el mismo nombre y dirección
        myCollection
            .whereEqualTo("nombre", nombreEmpresa)
            .whereEqualTo("direccion", direccion)
            .get()
            .addOnSuccessListener { querySnapshot ->
                if (!querySnapshot.isEmpty) {
                    // Ya existe un registro con el mismo nombre y dirección
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
                                "paginaweb" to binding.ETPWBEmp.text.toString(),
                                "urlImagen" to ""
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

    /**
     * Método que muestra un mensaje de resultado de la operación.
     */
    private fun resultadoOperacion(mensaje: String) {
        // Limpiar campos de entrada
        binding.ETID.setText("")
        binding.ETCiuEmpr.setText("")
        binding.ETNomEmpr.setText("")
        binding.ETPWBEmp.setText("")
        binding.ETTLF.setText("")
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }
}