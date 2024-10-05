package com.example.probird

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.content.res.AppCompatResources.getDrawable
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.probird.R.drawable
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.common.location.AccuracyLevel
import com.mapbox.common.location.DeviceLocationProvider
import com.mapbox.common.location.IntervalSettings
import com.mapbox.common.location.Location
import com.mapbox.common.location.LocationObserver
import com.mapbox.common.location.LocationProviderRequest
import com.mapbox.common.location.LocationService
import com.mapbox.common.location.LocationServiceFactory
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolylineAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createCircleAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolylineAnnotationManager
import com.mapbox.maps.plugin.gestures.addOnMapClickListener
import com.mapbox.maps.plugin.locationcomponent.location

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() ,PermissionsListener {
    private lateinit var btnExplore: TextView
    private lateinit var mapView: MapView
    private lateinit var btnZoomIn: ImageView
    private lateinit var btnZoomOut: ImageView
    private var param1: String? = null
    private var param2: String? = null
    lateinit var permissionsManager: PermissionsManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home_fragment, container, false)
        mapView = view.findViewById(R.id.mapView)
        btnExplore = view.findViewById(R.id.btnExplore)
        btnZoomIn = view.findViewById(R.id.btnZoomIn)
        btnZoomOut = view.findViewById(R.id.btnZoomOut)
        val locationService : LocationService = LocationServiceFactory.getOrCreate()
        var locationProvider: DeviceLocationProvider? = null



        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Permission sensitive logic called here, such as activating the Maps SDK's LocationComponent to show the device's location
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(28.218370, -25.731340))  // Default center
                    .pitch(0.0)
                    .zoom(14.0)
                    .bearing(180.0)
                    .build()
            )

             mapView.location.enabled = true

            val request = LocationProviderRequest.Builder()
                .interval(IntervalSettings.Builder().interval(0L).minimumInterval(0L).maximumInterval(0L).build())
                .displacement(0F)
                .accuracy(AccuracyLevel.HIGHEST)
                .build();

            val result = locationService.getDeviceLocationProvider(request)
            if (result.isValue) {
                locationProvider = result.value!!
            } else {
                Log.v("eBird","Failed to get device location provider")
            }

            val locationObserver = object: LocationObserver {
                override fun onLocationUpdateReceived(locations: MutableList<Location>) {
                    Log.v("eBird", "Location update received: " + locations)

                        val locations1 = locations.get(0)

                    //mapView.mapboxMap.setCamera(
                        CameraOptions.Builder().center(Point.fromLngLat(locations1.longitude,locations1.latitude))
                            .build()


                }
            }
            val annotationApi = mapView?.annotations
            val pointAnnotationManager = annotationApi?.createPointAnnotationManager()
            //locationProvider?.addLocationObserver(locationObserver)

            mapView.mapboxMap.addOnMapClickListener{
  pointAnnotationManager?.deleteAll()

// Set options for the resulting symbol layer.
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    // Define a geographic coordinate.
                    .withPoint(it)
                    // Specify the bitmap you assigned to the point annotation
                    // The bitmap will be added to map style automatically.
                    //.withIconImage(getDrawable(R.drawable.bird)!!.toBitmap(60 , 60))
                    .withDraggable(true)

// Add the resulting pointAnnotation to the map.
                pointAnnotationManager?.create(pointAnnotationOptions)

                pointAnnotationManager?.deleteAll()

                val  polylineAnnotationManager = annotationApi?.createPolylineAnnotationManager()
// Define a list of geographic coordinates to be connected.
                val points = listOf(
                    Point.fromLngLat(28.218370, -25.731340),
                       it


                )
// Set options for the resulting line layer.
                val polylineAnnotationOptions: PolylineAnnotationOptions = PolylineAnnotationOptions()
                    .withPoints(points)
                    // Style the line that will be added to the map.
                    .withLineColor("#ee4e8b")
                    .withLineWidth(5.0)
// Add the resulting line to the map.
                polylineAnnotationManager?.create(polylineAnnotationOptions)


                false
            }

            val circleAnnotationManager = annotationApi?.createCircleAnnotationManager()
// Set options for the resulting circle layer.
            val circleAnnotationOptions: CircleAnnotationOptions = CircleAnnotationOptions()
                // Define a geographic coordinate.
                .withPoint(Point.fromLngLat(28.218370, -25.731340))
                // Style the circle that will be added to the map.
                .withCircleRadius(80.0)
                .withCircleColor(Color.LTGRAY)
                .withCircleStrokeWidth(8.0)
                .withCircleStrokeColor(Color.BLUE)
                .withDraggable(true)
                .withCircleOpacity(0.2)
                .withCircleStrokeOpacity(0.3)
// Add the resulting circle to the map.
            circleAnnotationManager?.create(circleAnnotationOptions)



        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }



            btnZoomIn.setOnClickListener {
                val camState = mapView.mapboxMap.cameraState
                val currentZoom = camState.zoom

                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder().zoom(currentZoom + 1)
                        .build()
                )
            }


            btnZoomOut.setOnClickListener {
                val camState = mapView.mapboxMap.cameraState
                val currentZoom = camState.zoom

                mapView.mapboxMap.setCamera(
                    CameraOptions.Builder().zoom(currentZoom - 1)
                        .build()
                )
            }





            btnExplore.setOnClickListener {
                val intent = Intent(requireContext(), ExploreBird::class.java)
                startActivity(intent)
            }

            return view
        }




    companion object {
            private const val LOCATION_PERMISSION_REQUEST_CODE = 1001

            @JvmStatic
            fun newInstance(param1: String, param2: String) =
                HomeFragment().apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
        }

        override fun onExplanationNeeded(permissionsToExplain: List<String>) {

        }

        override fun onPermissionResult(granted: Boolean) {

        }
    }

private fun PermissionsManager.requestLocationPermissions(activity: HomeFragment) {

}

private fun PermissionsManager.Companion.areLocationPermissionsGranted(context: HomeFragment): Boolean {
    return PermissionsManager.areLocationPermissionsGranted(context)
}
