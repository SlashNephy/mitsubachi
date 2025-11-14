package blue.starry.mitsubachi.di

import blue.starry.mitsubachi.core.domain.ApplicationScope
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object ApplicationCoroutineScopeModule {
  @Singleton
  @Provides
  @ApplicationScope
  fun provide(): CoroutineScope {
    return CoroutineScope(SupervisorJob() + Dispatchers.Default)
  }
}
