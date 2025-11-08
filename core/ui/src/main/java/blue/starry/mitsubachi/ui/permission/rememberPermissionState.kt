package blue.starry.mitsubachi.ui.permission

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@Composable
@OptIn(ExperimentalPermissionsApi::class)
fun rememberPermissionState(permission: AndroidPermission): PermissionState {
  val isRequesting = remember { mutableStateOf(false) }
  val underlying = rememberMultiplePermissionsState(
    permissions = permission.requirement.entitlements,
    onPermissionsResult = {
      isRequesting.value = false
    },
  )

  val isGranted = remember(permission) {
    derivedStateOf {
      when (permission.requirement) {
        is PermissionRequirement.Some -> underlying.permissions.any { it.status.isGranted }
        is PermissionRequirement.Every -> underlying.permissions.all { it.status.isGranted }
      }
    }
  }

  val status = remember {
    derivedStateOf {
      when {
        isRequesting.value -> PermissionStatus.Requesting
        isGranted.value -> PermissionStatus.Granted
        else -> PermissionStatus.Denied
      }
    }
  }

  return remember(permission) {
    object : PermissionState {
      override val permission: AndroidPermission = permission
      override val status: PermissionStatus by status

      override fun launchPermissionRequester() {
        isRequesting.value = true
        underlying.launchMultiplePermissionRequest()
      }
    }
  }
}
