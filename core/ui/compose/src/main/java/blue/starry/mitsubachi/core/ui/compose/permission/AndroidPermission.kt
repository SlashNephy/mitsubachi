package blue.starry.mitsubachi.core.ui.compose.permission

import android.Manifest

sealed interface AndroidPermission {
  val requirement: PermissionRequirement

  data object Location : AndroidPermission {
    override val requirement = PermissionRequirement.Some(
      listOf(
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_FINE_LOCATION,
      ),
    )
  }
}
