package blue.starry.mitsubachi.ui.feature.checkin

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
@OptIn(FlowPreview::class)
class NearbyVenuesScreenTopBarViewModel @Inject constructor() : ViewModel() {
  private val _query = MutableStateFlow<String?>(null)
  val query = _query
    .asStateFlow()
    .filterNotNull()
    .filter { it.isNotBlank() }
    .distinctUntilChanged()

  fun onSearchQueryChanged(query: String) {
    _query.value = query
  }

  suspend fun onSearchQueryChangedDebounced(query: String, duration: Duration = 500.milliseconds) {
    delay(duration)
    onSearchQueryChanged(query)
  }
}
