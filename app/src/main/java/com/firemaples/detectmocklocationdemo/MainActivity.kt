package com.firemaples.detectmocklocationdemo

import android.Manifest
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.baidu.mapapi.SDKInitializer
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SDKInitializer.initialize(this.applicationContext)
//        SDKInitializer.setCoordType(CoordType.BD09LL)

        setViews()
        invokePermissions()
    }


    private fun setViews() {
        rg_mapTypes.setOnCheckedChangeListener { _, _ ->
            updateMap()
        }
    }

    private fun invokePermissions() {
        Dexter.withActivity(this)
            .withPermissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                    updateMap()
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>?,
                    token: PermissionToken?
                ) {
                }

            })
            .check()
    }

    private fun updateMap() {
        supportFragmentManager.beginTransaction().replace(
            R.id.fragment_map,
            if (rb_googleMap.isChecked) GoogleMapFragment() else BaiduMapFragment()
        ).commitAllowingStateLoss()
    }
}
