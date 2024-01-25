package com.example.stocker2

import android.Manifest
import android.R
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.stocker2.databinding.ActivityProductosMercadoBinding
import com.example.stocker2.databinding.ActivityRegistroMapasBinding
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
import org.osmdroid.views.overlay.compass.CompassOverlay
import org.osmdroid.views.overlay.compass.InternalCompassOrientationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay

class RegistroMapas : AppCompatActivity() {

    private lateinit var map: MapView
    private val PETICION_PERMISOS_OSM=0
    private lateinit var locListener: LocationListener
    private lateinit var locManager: LocationManager
    private lateinit var mLocationOverlay: MyLocationNewOverlay
    lateinit var posicion_new: GeoPoint
    private lateinit var marker: Marker
    private lateinit var binding:ActivityRegistroMapasBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
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

        map = binding.map

        generarMapa()
    //    añadirAccionesMapa()
        quitarRepeticionYLimitarScroll()
        // habilitarMiLocalizacion()
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
    private fun añadirMarcadorConAccion(posicion_new:GeoPoint,tituloP: String,contenidoP: String): ItemizedIconOverlay<OverlayItem> {
        return ItemizedIconOverlay(
            ArrayList<OverlayItem>().apply {
                val marker=Marker(map)
                marker.position=posicion_new
                marker.title=tituloP
                marker.snippet=contenidoP
                marker.icon=ContextCompat.getDrawable(map.context, R.drawable.ic_menu_camera)
                val overlayItem= OverlayItem(tituloP,contenidoP,posicion_new)
                overlayItem.setMarker(marker.icon)
                add(overlayItem)

            },
            object: ItemizedIconOverlay.OnItemGestureListener<OverlayItem>{
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    var geoPoint=GeoPoint(item.point.latitude,item.point.longitude)

                    return true
                }
                override fun onItemLongPress(index: Int, item: OverlayItem?): Boolean {
                    return false
                }
            },
            map.context
        )
    }
  /*  private fun añadirAccionesMapa(){
        val mapEventsReceiver=object: MapEventsReceiver {
            override fun singleTapConfirmedHelper(loc: GeoPoint?): Boolean {
                if(loc!=null){
                    var contenido = loc?.latitude.toString()+" "+loc?.longitude.toString()
                    añadirMarcadorConAccion(loc,"SuperMercado Registrado",contenido)
                }
                return true
            }

            override fun longPressHelper(loc: GeoPoint?): Boolean{
                if(loc!=null){
                    var contenido = loc?.latitude.toString()+" "+loc?.longitude.toString()
                    map.overlays.add(añadirMarcadorConAccion(loc,"Punto",contenido))
                }
                return false

            }
        }
        val mapEventsOverlay= MapEventsOverlay(mapEventsReceiver)
        map.overlays.add(0,mapEventsOverlay)
        map.invalidate()
    }
    */

    private fun generarMapa(){
        Configuration.getInstance().load(this, getSharedPreferences(packageName+"osmdroid", Context.MODE_PRIVATE))
        map.minZoomLevel=4.0
        map.controller.setZoom(12.0)
        // var startPoint=GeoPoint(35.6804, 139.7690) // Elimina o comenta esta línea
        // map.controller.setCenter(startPoint) // Elimina o comenta esta línea
        map.zoomController.setVisibility(CustomZoomButtonsController.Visibility.ALWAYS)
        map.setMultiTouchControls(true)
        var mCompassOverlay= CompassOverlay(this, InternalCompassOrientationProvider(this),map)
        mCompassOverlay.enableCompass()
        map.getOverlays().add(mCompassOverlay)
        map.setTileSource(TileSourceFactory.MAPNIK)
    }

}