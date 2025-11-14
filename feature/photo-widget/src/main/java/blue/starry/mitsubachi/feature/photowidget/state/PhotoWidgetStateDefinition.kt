package blue.starry.mitsubachi.feature.photowidget.state

import blue.starry.mitsubachi.core.ui.glance.state.WidgetStateDefinition

val PhotoWidgetStateDefinition = WidgetStateDefinition(
  dataStorePathPrefix = "photo_widget",
  defaultValue = PhotoWidgetState.Loading,
  kSerializer = PhotoWidgetState.serializer(),
)
