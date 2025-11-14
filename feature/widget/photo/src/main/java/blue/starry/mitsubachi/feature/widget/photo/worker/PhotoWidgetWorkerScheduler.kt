package blue.starry.mitsubachi.feature.widget.photo.worker

// TODO: いずれ domain/usecase に移すかも
interface PhotoWidgetWorkerScheduler {
  fun enqueue()
  fun cancel()
}
