package blue.starry.mitsubachi.feature.photowidget.worker

// TODO: いずれ domain/usecase に移すかも
interface PhotoWidgetWorkerScheduler {
  fun enqueue()
  fun cancel()
}
