package blue.starry.mitsubachi.core.ui.common.di

import blue.starry.mitsubachi.core.ui.common.deeplink.DeepLinkBuilder
import blue.starry.mitsubachi.core.ui.common.deeplink.DeepLinkBuilderImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DeepLinkBuilderModule {
  @Binds
  @Singleton
  abstract fun bind(impl: DeepLinkBuilderImpl): DeepLinkBuilder
}
