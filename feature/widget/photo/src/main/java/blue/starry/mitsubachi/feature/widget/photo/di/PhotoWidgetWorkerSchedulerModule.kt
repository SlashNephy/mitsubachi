package blue.starry.mitsubachi.feature.widget.photo.di

import blue.starry.mitsubachi.core.domain.usecase.PhotoWidgetWorkerScheduler
import blue.starry.mitsubachi.feature.widget.photo.worker.PhotoWidgetWorkerSchedulerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class PhotoWidgetWorkerSchedulerModule {
  @Binds
  @Singleton
  abstract fun bind(impl: PhotoWidgetWorkerSchedulerImpl): PhotoWidgetWorkerScheduler
}
