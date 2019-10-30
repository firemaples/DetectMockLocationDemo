package com.firemaples.detectmocklocationdemo

import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_google_map.*
import kotlinx.android.synthetic.main.view_info.*
import java.text.SimpleDateFormat
import java.util.*

class GoogleMapFragment : BaseMapFragment() {
    private val locationClient by lazy {
        LocationServices.getFusedLocationProviderClient(
            requireActivity()
        )
    }
    private var map: GoogleMap? = null
    private var marker: Marker? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_google_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setViews()

        locationClient.lastLocation.addOnSuccessListener {
            updateLastLocation(it)
        }
    }

    override fun onResume() {
        super.onResume()

        locationClient.requestLocationUpdates(
            LocationRequest().setInterval(3000),
            locationCallback,
            Looper.getMainLooper()
        )
    }

    override fun onPause() {
        super.onPause()
        locationClient.removeLocationUpdates(locationCallback)
    }

    private fun setViews() {
        val mapFragment = SupportMapFragment.newInstance()
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container, mapFragment)
            .commitAllowingStateLoss()
        mapFragment.getMapAsync {
            onMapReady(it)
        }
    }

    private fun onMapReady(map: GoogleMap) {
        Logger.debug("Google map ready")
        this.map = map
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            updateLastLocation(locationResult?.lastLocation)
        }
    }

    private fun updateLastLocation(location: Location?) {
        if (location == null) return
        Logger.info("Get Google location update: $location")

        val map = this.map
        val marker = this.marker
        if (map != null) {
            val latLng = location.toLatLng()
            if (marker == null) {
                this.marker = map.addMarker(MarkerOptions().position(latLng))
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
            } else {
                marker.position = latLng
                map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
            }
        }

        val mocked =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                location.isFromMockProvider
            } else {
                Settings.Secure.getString(
                    requireActivity().contentResolver,
                    Settings.Secure.ALLOW_MOCK_LOCATION
                ) != "0"
            }

        updateMockedStatus(mocked)
        updateLocationInfo(location.latitude, location.longitude, location.time)
    }

    private fun Location.toLatLng(): LatLng = LatLng(this.latitude, this.longitude)
}