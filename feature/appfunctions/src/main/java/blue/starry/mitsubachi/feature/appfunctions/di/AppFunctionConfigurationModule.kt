package blue.starry.mitsubachi.feature.appfunctions.di

import androidx.appfunctions.service.AppFunctionConfiguration
import blue.starry.mitsubachi.feature.appfunctions.ExampleFunctions
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object AppFunctionConfigurationModule {
  @Provides
  @Singleton
  fun provide(exampleFunctions: ExampleFunctions): AppFunctionConfiguration {
    return AppFunctionConfiguration.Builder()
      .addEnclosingClassFactory(ExampleFunctions::class.java) {
        exampleFunctions
      }
      .build()
  }
}
