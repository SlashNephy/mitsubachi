package blue.starry.mitsubachi.ui.permission

sealed interface PermissionRequirement {
  val entitlements: List<String>

  data class Some(override val entitlements: List<String>) : PermissionRequirement
  data class Every(override val entitlements: List<String>) : PermissionRequirement
}
