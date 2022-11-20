package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.AuthorizationMapper
import pl.sggw.sggwmeet.mapper.PlacesMapper
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object MapperModule {

    @Singleton
    @Provides
    fun provideAuthorizationMapper() : AuthorizationMapper {
        return AuthorizationMapper()
    }

    @Singleton
    @Provides
    fun providePlacesMapper() : PlacesMapper {
        return PlacesMapper()
    }
}