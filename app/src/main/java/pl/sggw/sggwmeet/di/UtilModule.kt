package pl.sggw.sggwmeet.di

import android.content.Context
import android.location.LocationManager
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
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

    @Singleton
    @Provides
    fun provideGson() : Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideUserDataStore(gson: Gson, authorizationConnector: AuthorizationConnector, mapper: AuthorizationMapper,): UserDataStore {
        return UserDataStore(gson, authorizationConnector, mapper)
    }
}