package com.zebra.zebrakotlindemo.bridge

object CBridge {

    // Load the native library on startup
    init {
        System.loadLibrary("native-lib")
    }

    /**
     * A native method that is implemented by the
     * 'native-lib' native library, which is packaged
     * with this application.
     */
    external fun makeACrash(): Unit
}