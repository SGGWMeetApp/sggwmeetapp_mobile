package pl.sggw.sggwmeet.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pl.sggw.sggwmeet.util.ExecutionHelper
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object CoreModule {

    @Singleton
    @Provides
    fun provideTestString() : String {
        if(ExecutionHelper.isRunningInMockMode()) {
            return "I'm running on MOCK build!";
        }
        return "I'm running on PROD build!";
    }
}