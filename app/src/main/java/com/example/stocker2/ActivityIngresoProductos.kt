package com.example.stocker2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.media.MediaScannerConnection
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import com.example.stocker2.databinding.ActivityIngresoProductosBinding
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


/**
 * [ActivityIngresoProductos] es una actividad que permite a los usuarios ingresar y eliminar productos y
 * gestionar su cantidad en un supermercado específico.
 */
class ActivityIngresoProductos : AppCompatActivity() {

    private lateinit var binding: ActivityIngresoProductosBinding
    val PETICION_PERMISO_CAMARA=321
    private var fotoPath=""
    private lateinit var btn_atras: ImageView
    private val db = FirebaseFirestore.getInstance()
    private val myCollectionp = db.collection("Productos")
    private val myCollections = db.collection("supermercados")
    private val CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA2 = 5
    lateinit var activityResultLauncherRedimensionarImagen2: ActivityResultLauncher<Intent>
    /**
     * Método llamado cuando se crea la actividad.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inicialización de los elementos del diseño XML
        crearObjetosDelXml()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ),
                PETICION_PERMISO_CAMARA
            )
        }
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Obtiene el ID del supermercado desde el intent
        val objIntent: Intent = intent
        var id = objIntent.getStringExtra("id")!!
        // Configuración del botón para guardar productos
        binding.btnGuardarProducto.setOnClickListener {
            guardarRegistro(id)
            GlobalScope.launch {
                delay(1000)
                listarDocumento(id)
            }
        }

        // Lista el documento al iniciar la actividad
        listarDocumento(id)

        // Configuración del botón de retroceso
        btn_atras = findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener {
            finish()
        }
        binding.btnFotoCosa.setOnClickListener {
            Log.d("cam", "Botón btnFotoCosa presionado")
            if(packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)){
                dispatchTakePictureIntent(true,CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA2)
            }
        }
        activityResultLauncherRedimensionarImagen2=
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
                    result->
                if(result.data!= null){
                    if(result.resultCode== RESULT_OK){
                        setPicRedim2()
                        refreshGallery()
                        subirAFirestore(id)
                    }
                }
            }
    }
    private fun subirAFirestore(id:String){

        binding.imagenSuper.isDrawingCacheEnabled=true
        binding.imagenSuper.buildDrawingCache()
        val bitmap= (binding.imagenSuper.drawable as BitmapDrawable).bitmap
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos)
        val data=baos.toByteArray()



    }
    private fun setPicRedim2(){
        val targetW: Int=binding.imagenSuper.width
        val targetH: Int = binding.imagenSuper.height

        val bmOptions= BitmapFactory.Options()
        bmOptions.inJustDecodeBounds=true
        BitmapFactory.decodeFile(fotoPath,bmOptions)

        val photoW=bmOptions.outWidth
        val photoh= bmOptions.outHeight

        val scaleFactor=Math.min(photoW/targetW,photoh/targetH)
        bmOptions.inJustDecodeBounds=false
        bmOptions.inSampleSize=scaleFactor
        val bitmap= BitmapFactory.decodeFile(fotoPath,bmOptions)
        binding.imagenSuper.setImageBitmap(bitmap)
    }
    private fun refreshGallery(){
        val f = File(fotoPath)
        MediaScannerConnection.scanFile(this, arrayOf(f.toString()),null,null)
    }

    /**
     * Método que inicializa la vinculación con los elementos de diseño XML.
     */
    private fun crearObjetosDelXml() {
        binding = ActivityIngresoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
    private fun dispatchTakePictureIntent(galeria: Boolean, code: Int) {
        val Intent=Intent()
       Intent.action = MediaStore.ACTION_IMAGE_CAPTURE
        if (Intent.resolveActivity(packageManager)!=null){
            val photoFile = createImageFile(galeria)
                if(photoFile !=null){
                fotoPath=photoFile!!.absolutePath
            // Obtiene el URI para el archivo
            val photoURI = FileProvider.getUriForFile(
                this,
                "com.example.stockimg",
                photoFile
            )

            Intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    Log.d("cam", "metidoEnEl dispatch")
            when (code) {

                CAPTURA_IMAGEN_GUARDAR_GALERIA_REDIMENSIONADA2 -> activityResultLauncherRedimensionarImagen2.launch(Intent)
            }
        }}
    }


    /**
     * Método llamado para crear el menú de opciones en la barra de acción.
     */
    private fun createImageFile(galeria: Boolean): File?{

        var image: File? = null
        val timeStamp= SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName= "IMG_" + timeStamp+ "_"

        var storageDir: File? = null
        Log.d("cam","Metido en el createFile")
        storageDir =
            if(galeria) {
                File(
                    Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM)
                        .toString()+"/Camera/"
                )
            }
            else{
                getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            }
        if(!storageDir!!.exists()){
            storageDir.mkdirs()
        }
        try {
            image = File.createTempFile(
                imageFileName,
                ".jpeg",
                storageDir
            )
        } catch(e: IOException) {

        }
        return image
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main_act1, menu)
        return true
    }

    /**
     * Método llamado cuando se hace clic en un elemento del menú de opciones.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val objIntent: Intent = intent
        var PagWeb = objIntent.getStringExtra("PaginaWeb")
        var email = objIntent.getStringExtra("correo")
        return when (item.itemId) {
            R.id.Web -> {
                if (PagWeb != null) {
                    abrirPagina(PagWeb)
                }
                true
            }
            R.id.Contactaremail -> {
                if (email != null) {
                    mandarCorreo(email)
                }
                true
            }
            R.id.AcDe -> {
                val intent = Intent(this, AcercaDeActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    /**
     * Método que guarda el nuevo registro de producto en Firebase Firestore.
     */
    private fun guardarRegistro(id: String) {
        if (binding.etNombProd.text.toString().isEmpty() || binding.etCantProd.text.toString().isEmpty()) {
            resultadoOperacion("Ni el nombre del producto ni la cantidad pueden estar vacíos")
            listarDocumento(id)
            return
        }
        var producto = binding.etNombProd.text.toString().trim()
        myCollectionp.document(id).get()
            .addOnSuccessListener {
                if (it.contains(producto)) {
                    myCollectionp.document(id).set(
                        hashMapOf(
                            producto to (binding.etCantProd.text.toString().toInt() + it.get(producto).toString().toInt())
                        ), SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            eliminarSieso(id, producto)
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase Update Error", e.message, e)
                        }
                } else {
                    myCollectionp.document(id).set(
                        hashMapOf(
                            producto to (binding.etCantProd.text.toString().toInt())
                        ), SetOptions.merge()
                    )
                        .addOnSuccessListener {
                            resultadoOperacion("El producto se ha registrado correctamente")
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firebase Update Error", e.message, e)
                        }
                }
            }
    }

    /**
     * Método que elimina un producto si su cantidad es igual o menor a 0.
     */
    private fun eliminarSieso(id: String, nombreProducto: String) {
        myCollectionp.document(id).get()
            .addOnSuccessListener { documentSnapshot ->
                val cantidadActual = documentSnapshot.getLong(nombreProducto)

                if (cantidadActual != null) {
                    if (cantidadActual <= 0) {
                        // El producto tiene una cantidad igual o menor a 0, por lo que se eliminará
                        val data = HashMap<String, Any>()
                        data[nombreProducto] = FieldValue.delete()

                        myCollectionp.document(id)
                            .update(data)
                            .addOnSuccessListener {
                                resultadoOperacion("El producto se ha eliminado debido a que su cantidad ha bajado a 0.")
                            }
                            .addOnFailureListener { e ->
                                Log.e("Firebase Delete Error", e.message, e)
                            }
                    } else {
                        resultadoOperacion("El producto se ha editado correctamente")
                    }
                }
            }
    }

    /**
     * Método que lista los productos en un documento específico y los muestra en la interfaz de usuario.
     */
    private fun listarDocumento(nombreDocumento: String) {
        myCollectionp
            .document(nombreDocumento)
            .get()
            .addOnSuccessListener { resultado ->
                val data = resultado.data
                val linearLayoutContainer = findViewById<LinearLayout>(R.id.linearLayoutContainer)

                linearLayoutContainer.removeAllViews() // Limpiar vistas anteriores

                for (entry in data.orEmpty()) {
                    val key = entry.key
                    val value = entry.value.toString()

                    val textView = TextView(this)
                    textView.text = "$key: $value"
                    textView.setTextColor(Color.parseColor("#B68D8D"))
                    linearLayoutContainer.addView(textView)
                }
            }
    }

    /**
     * Método que muestra un mensaje de resultado de la operación.
     */
    private fun resultadoOperacion(mensaje: String) {
        binding.etCantProd.setText("")
        binding.etNombProd.setText("")
        Toast.makeText(this, mensaje, Toast.LENGTH_LONG).show()
    }

    /**
     * Método que abre una página web en un navegador externo.
     */
    private fun abrirPagina(PagWeb: String) {
        // Para ésta, el ACTION_VIEW va a buscar una página que abrir
        if (PagWeb.isEmpty() || PagWeb.isBlank()) {
            Toast.makeText(this, "No introdujiste ninguna página web al registrarte", Toast.LENGTH_LONG).show()
        } else {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(PagWeb)))
        }
    }

    /**
     * Método que abre una aplicación de correo para enviar un correo electrónico.
     */
    private fun mandarCorreo(email: String) {
        startActivity(
            Intent(Intent.ACTION_VIEW).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "Contacto a empresa")
                putExtra(Intent.EXTRA_TEXT, "")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            }
        )
    }
}