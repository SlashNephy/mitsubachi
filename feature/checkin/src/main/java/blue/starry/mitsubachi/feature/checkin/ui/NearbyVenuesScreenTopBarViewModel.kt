package blue.starry.mitsubachi.feature.checkin.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@HiltViewModel
@OptIn(FlowPreview::class)
class NearbyVenuesScreenTopBarViewModel @Inject constructor() : ViewModel() {
  private val _query = MutableStateFlow<String?>(null)

  val query = _query
    .filterNotNull()
    .filter { it.isNotBlank() }
    .debounce(500.milliseconds)
    .distinctUntilChanged()

  fun onSearchQueryChanged(query: String) {
    _query.value = query
  }
}
