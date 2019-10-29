package com.firemaples.detectmocklocationdemo

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.baidu.location.BDAbstractLocationListener
import com.baidu.location.BDLocation
import com.baidu.location.LocationClient
import com.baidu.location.LocationClientOption
import com.baidu.mapapi.map.BitmapDescriptorFactory
import com.baidu.mapapi.map.MapStatusUpdateFactory
import com.baidu.mapapi.map.Marker
import com.baidu.mapapi.map.MarkerOptions
import com.baidu.mapapi.model.LatLng
import kotlinx.android.synthetic.main.fragment_baidu_map.*
import java.text.SimpleDateFormat
import java.util.*

class BaiduMapFragment : Fragment() {
    private val locationClient by lazy {
        LocationClient(requireActivity().applicationContext, LocationClientOption().apply {
            isOpenGps = true
            coorType = "bd09ll"
            scanSpan = 3000
            enableSimulateGps = false
        })
    }
    private val mapView by lazy { baiduMap }
    private val map by lazy { mapView.map }
    private var marker: Marker? = null
    private val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_baidu_map, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        locationClient.start()
        updateLastLocation(locationClient.lastKnownLocation)
    }

    override fun onResume() {
        super.onResume()

        mapView.onResume()
        locationClient.registerLocationListener(locationCallback)
    }

    override fun onPause() {
        super.onPause()

        mapView.onPause()
        locationClient.unRegisterLocationListener(locationCallback)
    }

    override fun onDestroy() {
        super.onDestroy()

        mapView.onDestroy()
        locationClient.stop()
    }

    private val locationCallback = object : BDAbstractLocationListener() {
        override fun onReceiveLocation(location: BDLocation?) {
            updateLastLocation(location)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateLastLocation(location: BDLocation?) {
        if (location == null) return

        Logger.info("Get Baidu location update: ${location.latitude}, ${location.longitude}")
        val latLng = LatLng(location.latitude, location.longitude)
        val marker = this.marker
        if (marker == null) {
            val icon = BitmapDescriptorFactory.fromResource(R.drawable.map_marker)
            this.marker = map.addOverlay(MarkerOptions().position(latLng).icon(icon)) as Marker
            map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 17f))
        } else {
            marker.position = latLng
            map.animateMapStatus(MapStatusUpdateFactory.newLatLngZoom(latLng, 17f))
        }

        val info =
            "${location.latitude}, ${location.longitude}, ${dateFormatter.format(System.currentTimeMillis())}"
        tv_locationInfo.text = info
    }
}