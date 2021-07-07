package com.indialone.googlemapsdemo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.ktx.addCircle
import com.google.maps.android.ktx.addMarker
import com.google.maps.android.ktx.awaitMap
import com.google.maps.android.ktx.awaitMapLoad
import com.google.maps.android.ktx.model.markerOptions
import com.indialone.googlemapsdemo.place.Place
import com.indialone.googlemapsdemo.place.PlaceRenderer
import com.indialone.googlemapsdemo.place.PlacesReader
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var circle: Circle? = null

    private val places: List<Place> by lazy {
        PlacesReader(this).read()
    }

    private val bicycleIcon: BitmapDescriptor by lazy {
        val color = ContextCompat.getColor(this, R.color.design_default_color_primary)
        BitmapHelper.vectorToBitmap(this, R.drawable.ic_directions_bike_black_24dp, color)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // with ktx maps library

        val mapFragment =
            supportFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment

        lifecycleScope.launchWhenCreated {
            val googleMap = mapFragment.awaitMap()

            googleMap.awaitMapLoad()

            val bounds = LatLngBounds.builder()
            places.forEach { place ->
                bounds.include(place.latLng)
            }
            googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))

            addClusteredMarkers(googleMap)

        }


        /*
            * without ktx maps library

            val mapFragment =
                supportFragmentManager.findFragmentById(R.id.map_fragment) as? SupportMapFragment
            mapFragment?.getMapAsync { googleMap ->
                // Ensure all places are visible in the map
                googleMap.setOnMapLoadedCallback {
                    val bounds = LatLngBounds.builder()
                    places.forEach { bounds.include(it.latLng) }
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(), 20))
                }

                //addMarkers(googleMap)
                addClusteredMarkers(googleMap)
            }
         */

    }

    /*
     * without ktx

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker(
                MarkerOptions()
                    .title(place.name)
                    .position(place.latLng)
                    .icon(bicycleIcon)
            )

            marker.tag = place
        }
    }

     */

    private fun addMarkers(googleMap: GoogleMap) {
        places.forEach { place ->
            val marker = googleMap.addMarker {
                title(place.name)
                position(place.latLng)
                icon(bicycleIcon)
            }

            marker.tag = place
        }
    }


    private fun addClusteredMarkers(googleMap: GoogleMap) {
        // Create the ClusterManager class and set the custom renderer.
        val clusterManager = ClusterManager<Place>(this, googleMap)
        clusterManager.renderer =
            PlaceRenderer(
                this,
                googleMap,
                clusterManager
            )

        // Set custom info window adapter
//        clusterManager.markerCollection.setInfoWindowAdapter(MarkerInfoWindowAdapter(this))

        // Add the places to the ClusterManager.
        clusterManager.addItems(places)
        clusterManager.cluster()

        // Set ClusterManager as the OnCameraIdleListener so that it
        // can re-cluster when zooming in and out.
        googleMap.setOnCameraIdleListener {
            clusterManager.onCameraIdle()
        }

        // show polugon
        clusterManager.setOnClusterItemClickListener { item ->
            addCircle(googleMap, item)
            return@setOnClusterItemClickListener false
        }

//        googleMap.setOnCameraMoveStartedListener {
//            clusterManager.markerCollection.markers.forEach { it.alpha = 0.3f }
//            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 0.3f }
//        }

//        googleMap.setOnCameraIdleListener {
//            // When the camera stops moving, change the alpha value back to opaque.
//            clusterManager.markerCollection.markers.forEach { it.alpha = 1.0f }
//            clusterManager.clusterMarkerCollection.markers.forEach { it.alpha = 1.0f }
//
//            // Call clusterManager.onCameraIdle() when the camera stops moving so that reclustering
//            // can be performed when the camera stops moving.
//            clusterManager.onCameraIdle()
//        }

    }

    // with ktx map library
    private fun addCircle(googleMap: GoogleMap, item: Place) {
        circle?.remove()
        circle = googleMap.addCircle {
            center(item.latLng)
            radius(20.0)
            fillColor(ContextCompat.getColor(this@MainActivity, R.color.teal_700))
            strokeColor(ContextCompat.getColor(this@MainActivity, R.color.white))
        }

    }

    /*
        * without ktx map library

        private fun addCircle(googleMap: GoogleMap, item: Place) {
            circle?.remove()
            circle = googleMap.addCircle(
                CircleOptions()
                    .center(item.latLng)
                    .radius(1000.0)
                    .fillColor(ContextCompat.getColor(this, R.color.teal_700))
                    .strokeColor(ContextCompat.getColor(this, R.color.purple_700))
            )
    }

     */


}