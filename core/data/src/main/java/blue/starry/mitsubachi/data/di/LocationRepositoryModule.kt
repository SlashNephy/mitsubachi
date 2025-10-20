package blue.starry.mitsubachi.data.di

import android.content.Context
import blue.starry.mitsubachi.data.repository.DeviceLocationRepositoryImpl
import blue.starry.mitsubachi.domain.usecase.DeviceLocationRepository
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class LocationRepositoryModule {
  companion object {
    @Provides
    @Singleton
    fun provideFusedLocationProviderClient(@ApplicationContext context: Context): FusedLocationProviderClient {
      return LocationServices.getFusedLocationProviderClient(context)
    }
  }

  @Binds
  @Singleton
  abstract fun bind(impl: DeviceLocationRepositoryImpl): DeviceLocationRepository
}
