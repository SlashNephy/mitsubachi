package blue.starry.mitsubachi.ui.permission

interface PermissionState {
  val permission: AndroidPermission
  val status: PermissionStatus

  fun launchPermissionRequester()
}
