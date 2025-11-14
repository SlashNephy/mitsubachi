package blue.starry.mitsubachi.core.ui.compose.permission

sealed interface PermissionStatus {
  data object Requesting : PermissionStatus
  data object Granted : PermissionStatus
  data object Denied : PermissionStatus
}
