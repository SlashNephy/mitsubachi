package blue.starry.mitsubachi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSerializable
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.serialization.NavBackStackSerializer
import androidx.navigation3.runtime.serialization.NavKeySerializer

// NOTE: androidx.navigation3.runtime.rememberNavBackStack はジェネリクスを型引数に取らず不便なので自前で実装している
@Composable
fun <K : NavKey> rememberNavBackStack(initial: K): NavBackStack<K> {
  return rememberSerializable(
    serializer = NavBackStackSerializer(elementSerializer = NavKeySerializer()),
  ) {
    NavBackStack(initial)
  }
}
