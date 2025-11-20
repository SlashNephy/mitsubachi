package blue.starry.mitsubachi.feature.widget.photo.worker

// TODO: いずれ domain/usecase に移すかも
interface PhotoWidgetWorkerScheduler {
  suspend fun enqueue()
  suspend fun cancel()
}
