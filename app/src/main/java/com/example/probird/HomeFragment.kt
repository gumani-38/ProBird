package com.example.probird

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
class HomeFragment : Fragment() {
    private lateinit var btnExplore: TextView
    private lateinit var mapView: MapView
    private lateinit var btnZoomIn: ImageView
    private lateinit var btnZoomOut: ImageView
    private var param1: String? = null
    private var param2: String? = null

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

        // Set default map camera position
        mapView.mapboxMap.setCamera(
            CameraOptions.Builder()
                .center(Point.fromLngLat(28.218370, -25.731340))  // Default center
                .pitch(0.0)
                .zoom(14.0)
                .bearing(0.0)
                .build()
        )


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
}
