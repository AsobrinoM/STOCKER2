package com.example.stocker2

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stocker2.databinding.ActivityRegistroMapasBinding
import com.example.stocker2.databinding.ActivityVerEnMapaBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay


class VerEnMapa : AppCompatActivity() {
    private lateinit var binding: ActivityVerEnMapaBinding
    private lateinit var map: MapView
    private val PETICION_PERMISOS_OSM=0
    private lateinit var locListener: LocationListener
    private lateinit var locManager: LocationManager
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    lateinit var posicion_new: GeoPoint
    private lateinit var marker: Marker
    private var id: String? = null
    private var marcadorActual: Marker? = null
    private var nombre:String? =null
    private val db = FirebaseFirestore.getInstance()
    private val myCollections = db.collection("supermercados")
    private var ultimaUbicacionMarcador: GeoPoint? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        val objIntent: Intent = intent
        id = objIntent.getStringExtra("id")!!
        myCollections.document(id!!).get().addOnSuccessListener { result ->
            nombre = result.getString("nombre")
        }

        if(ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_DENIED||
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_DENIED||
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)== PackageManager.PERMISSION_DENIED||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET)== PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET
                ),PETICION_PERMISOS_OSM
            )
        }
        //  accionesParaBotones()
     //   habilitarLocalizacion()
      //  binding.buttonRegistro.isEnabled=false
       // map = binding.map

     //   binding.buttonRegistro.setOnClickListener {
     //       actualizarUbicacionEnFirebase()
     //       Toast.makeText(this, "Ubicación actualizada!", Toast.LENGTH_SHORT).show()
      //      finish()

        generarMapa()
        quitarRepeticionYLimitarScroll()
        habilitarMiLocalizacion()
        agregarMarcadorDelSupermercado()
    }
    private fun agregarMarcadorDelSupermercado() {
        myCollections.document(id!!).get().addOnSuccessListener { document ->
            val latitud = document.getDouble("latitudubi")
            val longitud = document.getDouble("longitudubi")
            val nombre = document.getString("nombre")

            if (latitud != null && longitud != null) {
                val ubicacionSupermercado = GeoPoint(latitud, longitud)
                if (nombre != null) {
                    anadirMarcador(ubicacionSupermercado, "Ubicación del Supermercado", nombre)
                    mLocationOverlay.disableFollowLocation()
                    centrarMapaEnMarcador(ubicacionSupermercado)
                }
            } else {
                Toast.makeText(this, "No se encontró la ubicación del supermercado", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun centrarMapaEnMarcador(ubicacion: GeoPoint) {
        map.controller.setCenter(ubicacion)
        map.controller.setZoom(15.0) // Ajusta este valor según sea necesario
    }

    private fun anadirMarcador(ubicacion: GeoPoint, titulo: String, nombre: String) {
        val overlayItem = OverlayItem(nombre, titulo, ubicacion).apply {
            setMarker(ContextCompat.getDrawable(this@VerEnMapa, android.R.drawable.btn_star_big_off))
        }

        // Crear un ItemizedIconOverlay y agregar el OverlayItem
        val itemizedIconOverlay = ItemizedIconOverlay(
            arrayListOf(overlayItem),
            object : ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    mostrarNombreSuper()
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    mostrarDatosSuper()
                    return true
                }
            },
            map.context
        )

        // Agregar el ItemizedIconOverlay al mapa
        map.overlays.add(itemizedIconOverlay)
        map.invalidate()
    }
    private fun mostrarNombreSuper(){
                val mensaje = "Nombre: $nombre"
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Nombre Del Supermercado:")
                    .setMessage(mensaje)
                    .setCancelable(true)
                    .show()
            }



    private fun mostrarDatosSuper() {
        // Comprobar si el ID está establecido
        if (id == null) {
            Toast.makeText(this, "ID del supermercado no está disponible", Toast.LENGTH_SHORT).show()
            return
        }

        // Consultar Firestore para obtener los datos del supermercado
        myCollections.document(id!!).get().addOnSuccessListener { document ->
            if (document.exists()) {

                val direccion = document.getString("direccion") ?: "Dirección no disponible"
                val paginaWeb = document.getString("paginaweb").let {
                    if (it.isNullOrEmpty()) "No tiene página web" else it
                }

                // Crear y mostrar el diálogo
                val mensaje = "Dirección: $direccion\n Página Web: $paginaWeb"
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Datos del Supermercado:")
                    .setMessage(mensaje)
                    .setCancelable(true)
                    .show()
            } else {
                Toast.makeText(this, "El supermercado no se encontró en la base de datos", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al obtener datos del supermercado", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onStop(){
        super.onStop()
        pararLocalizacion()
    }
    private fun pararLocalizacion() {
        locManager.removeUpdates(locListener)

        //mLocationOverlay.disableMyLocation()
    }


    override fun onPause() {
        super.onPause()
        map.onPause()
    }

    override fun onResume(){
        super.onResume()
        map.onResume()
    }
    private fun quitarRepeticionYLimitarScroll(){
        map.isHorizontalMapRepetitionEnabled=false
        map.isVerticalMapRepetitionEnabled=false
        map.setScrollableAreaLimitLatitude(
            MapView.getTileSystem().maxLatitude,
            MapView.getTileSystem().minLatitude,
            0
        )
        map.setScrollableAreaLimitLongitude(
            MapView.getTileSystem().minLongitude,
            MapView.getTileSystem().maxLongitude,
            0
        )
    }
    private fun generarMapa(){
        Configuration.getInstance().load(this, getSharedPreferences(packageName+"osmdroid", Context.MODE_PRIVATE))
        map.minZoomLevel=4.0
        map.controller.setZoom(12.0)

        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true)
        var mCompassOverlay= CompassOverlay(this, InternalCompassOrientationProvider(this),map)
        mCompassOverlay.enableCompass()
        map.getOverlays().add(mCompassOverlay)
        map.setTileSource(TileSourceFactory.MAPNIK)
    }

    @SuppressLint("MissingPermission")
    fun habilitarMiLocalizacion () {

        mLocationOverlay = MyLocationNewOverlay(GpsMyLocationProvider(this), map)
        mLocationOverlay.enableMyLocation()
        mLocationOverlay.enableFollowLocation()

        map.getOverlays().add(mLocationOverlay)
    }
    private fun crearObjetosDelXml() {
        binding = ActivityVerEnMapaBinding.inflate(layoutInflater)
        setContentView(binding.root)
        map = binding.map
    }
}