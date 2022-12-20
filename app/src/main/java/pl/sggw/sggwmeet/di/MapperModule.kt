package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.mapper.*
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

    @Singleton
    @Provides
    fun provideUserMapper() : UserMapper {
        return UserMapper()
    }

    @Singleton
    @Provides
    fun provideEventMapper() : EventMapper {
        return EventMapper()
    }

    @Singleton
    @Provides
    fun provideGroupMapper() : GroupMapper {
        return GroupMapper()
    }
}