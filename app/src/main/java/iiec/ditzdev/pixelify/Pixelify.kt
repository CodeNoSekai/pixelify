package iiec.ditzdev.pixelify

import android.app.Application
import iiec.ditzdev.pixelify.handler.CrashHandler

class Pixelify : Application() {
    override fun onCreate() {
        super.onCreate()
        CrashHandler.initialize(this)
    }
}