package blue.starry.mitsubachi.ui.di

import blue.starry.mitsubachi.ui.snackbar.SnackbarHostService
import blue.starry.mitsubachi.ui.snackbar.SnackbarHostServiceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class SnackbarHostServiceModule {
  @Binds
  @Singleton
  abstract fun bind(impl: SnackbarHostServiceImpl): SnackbarHostService
}
