package blue.starry.mitsubachi.ui.flow

import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

// NOTE: Presenter 層のロジックを含まないので :core:common に移動するのが適切かも

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface ResettableMutableStateFlow<T> : MutableStateFlow<T> {
  fun reset()
}

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
private class ResettableMutableStateFlowImpl<T>(
  private val initialValueFactory: () -> T,
) : ResettableMutableStateFlow<T>, MutableStateFlow<T> by MutableStateFlow(initialValueFactory()) {
  override fun reset() {
    update {
      initialValueFactory()
    }
  }
}

fun <T> ResettableMutableStateFlow(initialValue: T): ResettableMutableStateFlow<T> {
  return ResettableMutableStateFlowImpl { initialValue }
}

fun <T> ResettableMutableStateFlow(initialValueFactory: () -> T): ResettableMutableStateFlow<T> {
  return ResettableMutableStateFlowImpl(initialValueFactory)
}
