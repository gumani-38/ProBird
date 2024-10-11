package com.example.probird

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import androidx.fragment.app.Fragment
import com.example.api.api
import com.example.probird.models.NearbyHotspotItem
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.locationcomponent.OnIndicatorPositionChangedListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.lang.Exception

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment(), PermissionsListener {
    private lateinit var btnExplore: TextView
    private lateinit var mapView: MapView
    private lateinit var btnZoomIn: ImageView
    private lateinit var btnZoomOut: ImageView
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var permissionsManager: PermissionsManager
    private var userLocation: Point? = null // Track the user's location


    private var onIndicatorPositionChangedListener = OnIndicatorPositionChangedListener { point ->
        userLocation = point
        // Optionally move the camera to the user's location
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(point)
                .zoom(12.0)
                .build()
        )
    }

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

        if (PermissionsManager.areLocationPermissionsGranted(requireContext())) {
            setupMap() // Setup the map if permissions are already granted
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(requireActivity())
        }

        btnZoomIn.setOnClickListener {
            val camState = mapView.mapboxMap.cameraState
            val currentZoom = camState.zoom
            mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(currentZoom + 1).build())
        }

        btnZoomOut.setOnClickListener {
            val camState = mapView.mapboxMap.cameraState
            val currentZoom = camState.zoom
            mapView.mapboxMap.setCamera(CameraOptions.Builder().zoom(currentZoom - 1).build())
        }

        btnExplore.setOnClickListener {
            val intent = Intent(requireContext(), ExploreBird::class.java)
            startActivity(intent)
        }

        return view
    }

    // Setup the map view and location component
    private fun setupMap() {
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(28.218370, -25.731340)) // Default center
                .zoom(12.0)
                .build()
        )
        initializeLocationComponent()
        fetchBirdDataAndDisplayMarkers()
    }

    // Initialize location component and start observing user location
    private fun initializeLocationComponent() {
        val locationComponentPlugin = mapView.location
        locationComponentPlugin.updateSettings {
            enabled = true
            pulsingEnabled = true
        }

        // Register the location update listener
        locationComponentPlugin.addOnIndicatorPositionChangedListener(onIndicatorPositionChangedListener)
    }

    // Fetch bird data from the eBird API and display markers
    private fun fetchBirdDataAndDisplayMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.eBirdNearByHotspotRetrofit.getNearByHotspot("fcj0sukk3qm4", "json", -25.731340, 28.218370)

                if (response.isSuccessful) {
                    val responseBody = response.body() // Access the body

                    // Make sure the responseBody is not null before calling string()
                    if (responseBody != null) {
                        val responseData = responseBody.toString() // Correct usage of .string()
                        if (responseData.isNotEmpty()) {
                            Log.d("EBirdResponse", "Response Data: $responseData") // Debugging
                            val birdList = parseEBirdData(responseData)
                            withContext(Dispatchers.Main) {
                                addBirdMarkers(birdList)
                                adjustCameraToMarkers(birdList) // Adjust camera to markers
                            }
                        }
                    } else {
                        Log.e("ebird", "Empty response body")
                    }
                } else {
                    Log.e("ebird", "Error code: ${response.code()} - ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                Log.e("ebird", "Exception occurred: ${e.message}")
            }
        }
    }

    // Parse API response into a list of bird hotspots
    private fun parseEBirdData(responseData: String): List<NearbyHotspotItem> {
        val birdList = mutableListOf<NearbyHotspotItem>()
        val jsonArray = JSONArray(responseData)

        for (i in 0 until jsonArray.length()) {
            val birdJson = jsonArray.getJSONObject(i)
            val lat = birdJson.getDouble("lat")
            val lng = birdJson.getDouble("lng")
            val locName = birdJson.getString("locName")
            val countryCode = birdJson.getString("countryCode")
            val latestObsDt = birdJson.getString("latestObsDt")
            val locId = birdJson.getString("locId")
            val numSpeciesAllTime = birdJson.getInt("numSpeciesAllTime")
            val subnational1Code = birdJson.getString("subnational1Code")

            // Add the full NearbyHotspotItem with all fields
            birdList.add(
                NearbyHotspotItem(
                    countryCode = countryCode,
                    lat = lat,
                    latestObsDt = latestObsDt,
                    lng = lng,
                    locId = locId,
                    locName = locName,
                    numSpeciesAllTime = numSpeciesAllTime,
                    subnational1Code = subnational1Code
                )
            )
        }

        // Debugging parsed bird list
        birdList.forEach { bird ->
            Log.d("BirdData", "Lat: ${bird.lat}, Lng: ${bird.lng}, Name: ${bird.locName}")
        }

        return birdList
    }

    // Add bird markers to the map
    @SuppressLint("UseCompatLoadingForDrawables")
    private fun addBirdMarkers(birdList: List<NearbyHotspotItem>) {
        val annotationApi = mapView.annotations
        val pointAnnotationManager = annotationApi.createPointAnnotationManager()

        birdList.forEach { bird ->
            val birdIcon = requireContext().getDrawable(R.drawable.bird)
            if (birdIcon != null) {
                val pointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(bird.lng, bird.lat))
                    .withIconImage(birdIcon.toBitmap(60, 60)) // Update icon image size
                pointAnnotationManager.create(pointAnnotationOptions)
            } else {
                Log.e("BirdMarker", "Bird drawable is null!")
            }
        }
    }

    // Adjust camera to markers if present
    private fun adjustCameraToMarkers(birdList: List<NearbyHotspotItem>) {
        if (birdList.isNotEmpty()) {
            val firstBird = birdList.first()
            mapView.mapboxMap.setCamera(
                CameraOptions.Builder()
                    .center(Point.fromLngLat(firstBird.lng, firstBird.lat))
                    .zoom(10.0)  // Adjust the zoom level as needed
                    .build()
            )
        }
    }

    companion object {
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
        // Explain to the user why the permission is needed
    }

    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            setupMap() // Setup the map after permissions are granted
        } else {
            // Handle permission denial
        }
    }
}
