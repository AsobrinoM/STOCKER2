package com.example.stocker2

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Message
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stocker2.databinding.ActivityProductosMercadoBinding
import com.example.stocker2.databinding.ActivityRegistroMapasBinding
import com.google.firebase.firestore.FirebaseFirestore
import org.osmdroid.config.Configuration
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.CustomZoomButtonsController
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.MapEventsOverlay
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class RegistroMapas : AppCompatActivity() {

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


    private lateinit var binding:ActivityRegistroMapasBinding
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
        binding.buttonRegistro.isEnabled=false
        map = binding.map

        binding.buttonRegistro.setOnClickListener {
            actualizarUbicacionEnFirebase()
            Toast.makeText(this, "Ubicación actualizada!", Toast.LENGTH_SHORT).show()
            finish()
        }
        generarMapa()
        añadirAccionesMapa()
        quitarRepeticionYLimitarScroll()
         habilitarMiLocalizacion()
    }
    @SuppressLint("MissingPermission")
    fun habilitarMiLocalizacion () {

        locManager = this.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val loc: Location? = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0f, locListener)
    }

    private fun crearObjetosDelXml(){
        binding = ActivityRegistroMapasBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

    private fun añadirMarcador(posicion_new: GeoPoint, contenidoP: String) {
        // Eliminar el marcador actual si existe
        if (marcadorActual != null) {
            map.overlays.remove(marcadorActual)
        }

        // Crear y añadir el nuevo marcador
        val marker = Marker(map)
        marker.position = posicion_new
        ultimaUbicacionMarcador = posicion_new
        marker.title = nombre
        marker.snippet = contenidoP
        marker.icon = ContextCompat.getDrawable(map.context, R.drawable.btn_star)
        map.overlays.add(marker)
        binding.buttonRegistro.isEnabled=true
        // Actualizar la referencia al marcador actual
        marcadorActual = marker

        // Refrescar el mapa
        map.invalidate()
    }

    private fun añadirAccionesMapa(){
        val mapEventsReceiver=object: MapEventsReceiver {
            override fun singleTapConfirmedHelper(loc: GeoPoint?): Boolean {

                return true
            }

            override fun longPressHelper(loc: GeoPoint?): Boolean {
                loc?.let {
                    val contenido = "${it.latitude} ${it.longitude}"
                    añadirMarcador(it, "Este es tu supermercado")
                }
                return false
            }
        }
        val mapEventsOverlay = MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(0, mapEventsOverlay)
    }
    private fun actualizarUbicacionEnFirebase() {
        marcadorActual?.let { marcador ->
            myCollections.document(id!!)
                .update("latitudubi", marcador.position.latitude)
                .addOnSuccessListener {
                    Log.d("Firestore", "latitud actualizada correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al actualizar urlVideo", e)
                }
            myCollections.document(id!!)
                .update("longitudubi", marcador.position.longitude)
                .addOnSuccessListener {
                    Log.d("Firestore", "longitud actualizada correctamente")
                }
                .addOnFailureListener { e ->
                    Log.e("Firestore", "Error al actualizar urlVideo", e)
                }


    }
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

}