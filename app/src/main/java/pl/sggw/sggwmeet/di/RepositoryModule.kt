package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.*
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.*
import pl.sggw.sggwmeet.model.repository.*
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

    @Singleton
    @Provides
    fun provideGroupRepository(
        groupConnector: GroupConnector,
        groupMapper: GroupMapper,
        userDataStore: UserDataStore
    ) : GroupRepository {
        return GroupRepository(groupConnector, groupMapper, userDataStore)
    }

    @Singleton
    @Provides
    fun provideReviewRepository(
        reviewConnector: ReviewConnector,
        placesMapper: PlacesMapper,
        userDataStore: UserDataStore
    ) : ReviewRepository {
        return ReviewRepository(reviewConnector, placesMapper, userDataStore)
    }
}