package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.mapper.PlacesMapper
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.repository.AuthorizationRepository
import pl.sggw.sggwmeet.model.repository.PlacesRepository
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthorizationRepository(
        authorizationConnector: AuthorizationConnector,
        authorizationMapper: AuthorizationMapper
    ) : AuthorizationRepository {
        return AuthorizationRepository(authorizationConnector, authorizationMapper)
    }

    @Singleton
    @Provides
    fun providePlacesRepository(
        placesConnector: PlacesConnector,
        placesMapper: PlacesMapper,
        rootMarkerProvider: RootMarkerProvider
    ) : PlacesRepository {
        return PlacesRepository(placesConnector, placesMapper, rootMarkerProvider)
    }
}