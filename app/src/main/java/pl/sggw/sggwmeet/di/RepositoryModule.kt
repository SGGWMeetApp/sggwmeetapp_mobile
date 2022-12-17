package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.mapper.EventMapper
import pl.sggw.sggwmeet.mapper.PlacesMapper
import pl.sggw.sggwmeet.mapper.UserMapper
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.EventConnector
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.connector.UserConnector
import pl.sggw.sggwmeet.model.repository.AuthorizationRepository
import pl.sggw.sggwmeet.model.repository.EventRepository
import pl.sggw.sggwmeet.model.repository.PlacesRepository
import pl.sggw.sggwmeet.model.repository.UserRepository
import pl.sggw.sggwmeet.provider.RootMarkerProvider
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {

    @Singleton
    @Provides
    fun provideAuthorizationRepository(
        authorizationConnector: AuthorizationConnector,
        authorizationMapper: AuthorizationMapper,
        userDataStore: UserDataStore
    ) : AuthorizationRepository {
        return AuthorizationRepository(authorizationConnector, authorizationMapper, userDataStore)
    }

    @Singleton
    @Provides
    fun providePlacesRepository(
        placesConnector: PlacesConnector,
        placesMapper: PlacesMapper,
        rootMarkerProvider: RootMarkerProvider,
        userDataStore: UserDataStore
    ) : PlacesRepository {
        return PlacesRepository(placesConnector, placesMapper, rootMarkerProvider, userDataStore)
    }

    @Singleton
    @Provides
    fun provideUserRepository(
        userConnector: UserConnector,
        userMapper: UserMapper,
        userDataStore: UserDataStore
    ) : UserRepository {
        return UserRepository(userConnector, userMapper, userDataStore)
    }

    @Singleton
    @Provides
    fun provideEventRepository(
        eventConnector: EventConnector,
        eventMapper: EventMapper,
        userDataStore: UserDataStore
    ) : EventRepository {
        return EventRepository(eventConnector, eventMapper, userDataStore)
    }
}