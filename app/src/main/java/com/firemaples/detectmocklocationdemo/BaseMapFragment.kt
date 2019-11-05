package com.firemaples.detectmocklocationdemo

import android.view.View
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.view_info.*
import java.text.SimpleDateFormat
import java.util.*

open class BaseMapFragment : Fragment() {
    private val dateFormatter = SimpleDateFormat("HH:mm:ss", Locale.US)
    private var counter: Int = 0

    protected fun updateMockedStatus(isMocked: Boolean?) {
        tv_locationReal.visibility =
            if (isMocked == false) View.VISIBLE else View.INVISIBLE
        tv_locationFake.visibility =
            if (isMocked == true) View.VISIBLE else View.INVISIBLE
    }

    protected fun updateLocationInfo(lat: Double, lng: Double, time: Long) {
        val info =
            "${lat.toSixDigital()}, ${lng.toSixDigital()} ${dateFormatter.format(time)} (${counter++})"
        tv_locationInfo.text = info
    }

    protected fun updateErrorMsg(errorMsg: String?) {
        tv_errorMsg.text = errorMsg
    }

    private fun Number.toSixDigital(): String = "%.6f".format(Locale.US, this)
}