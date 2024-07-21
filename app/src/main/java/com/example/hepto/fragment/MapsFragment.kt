package com.example.hepto.fragment

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.VectorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.hepto.R
import com.example.hepto.viewmodel.MapsViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private val mapsViewModel: MapsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        mapsViewModel.polylineOptions.observe(viewLifecycleOwner) { polylineOptions ->
            mMap.addPolyline(polylineOptions)
        }

        val origin = "13.0827,80.2707"
        val destination = "8.9637,77.5374"
        val apiKey = "AIzaSyBIXyrnQ8aY9PscJ6QVbPtW0928F1yVhQ0"
        mapsViewModel.fetchDirections(origin, destination, apiKey)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        addMarkers()
    }

    private fun vectorToBitmap(vectorDrawable: VectorDrawable): Bitmap {
        val bitmap = Bitmap.createBitmap(vectorDrawable.intrinsicWidth, vectorDrawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }

    private fun addMarkers() {
        val locations = listOf(
            LatLng(13.0827, 80.2707),
            LatLng(8.9637, 77.5374)
        )

        val drawable = ContextCompat.getDrawable(requireContext(), R.drawable.custom_marker) as VectorDrawable
        val bitmap = vectorToBitmap(drawable)

        for (location in locations) {
            val markerOptions = MarkerOptions()
                .position(location)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap))
            mMap.addMarker(markerOptions)
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locations[0], 5f))
    }
}
