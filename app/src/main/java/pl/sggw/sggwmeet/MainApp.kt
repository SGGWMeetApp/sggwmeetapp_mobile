package pl.sggw.sggwmeet

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import io.easyprefs.Prefs

@HiltAndroidApp
class MainApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Prefs.initializeApp(this)
    }
}