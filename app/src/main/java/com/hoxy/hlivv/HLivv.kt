package com.hoxy.hlivv

import android.app.Application
import android.content.Context

/**
 * @author 반정현
 */
class HLivv : Application() {
    companion object {
        private lateinit var context: Context

        fun getContext(): Context {
            if (!::context.isInitialized) {
                throw IllegalStateException("Context has not been initialized in HLivv application class.")
            }
            return context
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}
