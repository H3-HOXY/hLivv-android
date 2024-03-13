package com.hoxy.hlivv.ui.unity.unity

class NativeUnityBridge {
    var onMenuExpend: () -> Unit = {}

    private fun expendMenu() {
        onMenuExpend()
    }

    companion object {
        @JvmStatic
        private var instance: NativeUnityBridge? = null

        @JvmStatic
        fun getInstance(): NativeUnityBridge {
            if (instance == null) {
                instance = NativeUnityBridge()
            }
            return instance!!
        }
    }
}