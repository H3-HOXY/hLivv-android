package com.hoxy.hlivv.domain

import android.content.Context
import android.content.res.Configuration


object CameraUtils {
    fun isPortraitMode(context: Context): Boolean =
        context.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

}