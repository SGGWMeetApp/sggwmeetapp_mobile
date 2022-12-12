package pl.sggw.sggwmeet.di

import android.util.Log
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import pl.sggw.sggwmeet.model.UserDataStore
import pl.sggw.sggwmeet.model.connector.AuthorizationConnector
import pl.sggw.sggwmeet.model.connector.PlacesConnector
import pl.sggw.sggwmeet.model.connector.mock.MockAuthorizationConnector
import pl.sggw.sggwmeet.model.connector.mock.MockPlacesConnector
import pl.sggw.sggwmeet.util.ExecutionHelper
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object ConnectorModule {

    private const val BACKEND_BASE_URL = "http://3.68.195.28"
    private const val USER_TOKEN_HEADER = "User-Token"
    private const val AUTHORIZATION_HEADER = "Authorization"

    @Singleton
    @Provides
    fun provideAuthorizationConnector(@NonAuthorizedRetrofitInstance retrofit : Retrofit) : AuthorizationConnector {
        return restConnectorOrMock(
            retrofit.create(AuthorizationConnector::class.java),
            MockAuthorizationConnector()
        )
    }

    @Singleton
    @Provides
    fun providePlacesConnector(@AuthorizedRetrofitInstance retrofit : Retrofit) : PlacesConnector {
        return restConnectorOrMock(
            retrofit.create(PlacesConnector::class.java),
            MockPlacesConnector()
        )
    }

    private fun <T, T1 : T, T2 : T> restConnectorOrMock(restConnector : T1, mockConnector : T2) : T {
        if(ExecutionHelper.isRunningInMockMode()) {
            return mockConnector
        }
        return restConnector
    }

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class NonAuthorizedRetrofitInstance

    @Qualifier
    @Retention(AnnotationRetention.BINARY)
    annotation class AuthorizedRetrofitInstance

    /**
     * Simple Retrofit HTTP connector which logs requests/responses. No additional authorization headers are applied
     */
    @Singleton
    @Provides
    @NonAuthorizedRetrofitInstance
    fun provideNonAuthorizedRetrofitInstance() : Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Retrofit HTTP connector with authorization.
     * Authorization headers are applied automatically using 'SharedPreferences' user data storage to generate auth tokens
     */
    @Singleton
    @Provides
    @AuthorizedRetrofitInstance
    fun provideAuthorizedRetrofitInstance(userDataStore: UserDataStore) : Retrofit {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val httpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(loggingInterceptor)
            .addInterceptor(userTokenInterceptor(userDataStore))
            .build()

        return Retrofit.Builder()
            .baseUrl(BACKEND_BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun userTokenInterceptor(userDataStore: UserDataStore) : Interceptor {
        return Interceptor {

            val token = runBlocking {
                return@runBlocking userDataStore.getToken()
            }

            Log.d("AuthInterceptor", "Retrieved token : $token")
            val request = it.request().newBuilder()
                .addHeader(AUTHORIZATION_HEADER, "Bearer $token")
                .addHeader(USER_TOKEN_HEADER, token)
                .build()
            return@Interceptor it.proceed(request);
        }
    }
}