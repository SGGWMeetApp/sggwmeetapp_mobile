package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.mock.MockAuthorizationConnector
import pl.sggw.sggwmeet.model.connector.rest.RestAuthorizationConnector
import pl.sggw.sggwmeet.util.ExecutionHelper
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ConnectorModule {

    @Singleton
    @Provides
    fun provideAuthorizationConnector() : AuthorizationConnector {
        return restConnectorOrMock(
            RestAuthorizationConnector(),
            MockAuthorizationConnector()
        )
    }

    private fun <T, T1 : T, T2 : T> restConnectorOrMock(restConnector : T1, mockConnector : T2) : T {
        if(ExecutionHelper.isRunningInMockMode()) {
            return mockConnector
        }
        return restConnector
    }
}