package blue.starry.mitsubachi.core.ui.compose.permission

interface PermissionState {
  val permission: AndroidPermission
  val status: PermissionStatus

  fun launchPermissionRequester()
}
