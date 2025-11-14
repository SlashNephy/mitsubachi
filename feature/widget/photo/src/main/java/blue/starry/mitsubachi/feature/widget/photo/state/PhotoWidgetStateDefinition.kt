package blue.starry.mitsubachi.feature.widget.photo.state

import blue.starry.mitsubachi.core.ui.glance.state.WidgetStateDefinition

val PhotoWidgetStateDefinition = WidgetStateDefinition(
  dataStorePathPrefix = "photo_widget",
  defaultValue = PhotoWidgetState.Loading,
  kSerializer = PhotoWidgetState.serializer(),
)
