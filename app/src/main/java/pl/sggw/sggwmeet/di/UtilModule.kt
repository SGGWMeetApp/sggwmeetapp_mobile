package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UtilModule {

    @Singleton
    @Provides
    fun provideRootMarkerProvider() : RootMarkerProvider {
        return RootMarkerProvider()
    }
}