package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.repository.AuthorizationRepository
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
}