package com.example.probird

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.Fragment
import com.example.api.api
import com.example.probird.models.BirdTaxonomyItem
import com.example.probird.models.EbirdHotspot
import com.example.probird.models.EbirdHotspotItem
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.locationcomponent.location
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
                .zoom(8.0)
                .build()
        )
        fetchBirdDataAndDisplayMarkers()
    }

    private fun bitmapFromDrawableRes(context: Context, @DrawableRes resourceId: Int) =
        convertDrawableToBitmap(AppCompatResources.getDrawable(context, resourceId))

    private fun convertDrawableToBitmap(sourceDrawable: Drawable?): Bitmap? {
        if (sourceDrawable == null) {
            return null
        }
        return if (sourceDrawable is BitmapDrawable) {
            sourceDrawable.bitmap
        } else {
// copying drawable object to not manipulate on the same reference
            val constantState = sourceDrawable.constantState ?: return null
            val drawable = constantState.newDrawable().mutate()

            val bitmap: Bitmap = Bitmap.createBitmap(
                drawable.intrinsicWidth, drawable.intrinsicHeight,
                Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(bitmap)
            drawable.setBounds(0, 0, canvas.width, canvas.height)
            drawable.draw(canvas)
            bitmap
        }
    }
    // Fetch bird data from the eBird API and display markers
    private fun fetchBirdDataAndDisplayMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = api.eBirdNearByHotspotRetrofit.getNearByHotspot("fcj0sukk3qm4", "json", -25.731340, 28.218370)

                if (response.isSuccessful) {
                    val hotspotArr: List<EbirdHotspotItem> = response.body() ?: emptyList()
                        withContext(Dispatchers.Main) {
                             parseEBirdData(hotspotArr)
                    }
                }
            } catch (e: Exception) {
                Log.e("ebird", "Exception occurred: ${e.message}")
            }
        }
    }



    private fun parseEBirdData(hotspotArr: List<EbirdHotspotItem>) {
        try {
            for (hotspot in hotspotArr) {
           bitmapFromDrawableRes(
                requireContext(),
                R.drawable.bird_marker
            )?.let {
                val annotationApi = mapView?.annotations
                val pointAnnotationManager = annotationApi?.createPointAnnotationManager()
                val pointAnnotationOptions: PointAnnotationOptions = PointAnnotationOptions()
                    .withPoint(Point.fromLngLat(hotspot.lng, hotspot.lat))
                    .withIconImage(it)
                pointAnnotationManager?.create(pointAnnotationOptions)
            }

        }
        } catch (e: Exception) {
            Log.e("MapError", "Error occurred while adding markers: ${e.message}")
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
