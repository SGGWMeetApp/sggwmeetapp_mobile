package pl.sggw.sggwmeet.di

import android.content.Context
import android.location.LocationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import pl.sggw.sggwmeet.util.MarkerBitmapGenerator
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Singleton
    @Provides
    fun provideRootMarkerProvider() : RootMarkerProvider {
        return RootMarkerProvider()
    }

    @Singleton
    @Provides
    fun provideBitmapGenerator(@ApplicationContext context : Context) : MarkerBitmapGenerator {
        return MarkerBitmapGenerator(context)
    }

    @Singleton
    @Provides
    fun provideLocationManager(@ApplicationContext context: Context): LocationManager {
        return context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }
}