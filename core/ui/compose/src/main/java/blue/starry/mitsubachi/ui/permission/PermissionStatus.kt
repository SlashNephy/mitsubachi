package blue.starry.mitsubachi.ui.permission

sealed interface PermissionStatus {
  data object Requesting : PermissionStatus
  data object Granted : PermissionStatus
  data object Denied : PermissionStatus
}
