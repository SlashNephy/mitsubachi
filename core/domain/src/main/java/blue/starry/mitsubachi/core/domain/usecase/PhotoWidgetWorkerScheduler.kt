package blue.starry.mitsubachi.core.domain.usecase

interface PhotoWidgetWorkerScheduler {
  suspend fun enqueue()
  suspend fun cancel()
}
