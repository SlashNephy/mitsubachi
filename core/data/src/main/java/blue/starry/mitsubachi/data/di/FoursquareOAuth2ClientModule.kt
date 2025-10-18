package blue.starry.mitsubachi.data.di

import blue.starry.mitsubachi.data.network.FoursquareOAuth2ClientImpl
import blue.starry.mitsubachi.domain.usecase.FoursquareOAuth2Client
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FoursquareOAuth2ClientModule {
  @Binds
  @Singleton
  abstract fun bind(impl: FoursquareOAuth2ClientImpl): FoursquareOAuth2Client
}
