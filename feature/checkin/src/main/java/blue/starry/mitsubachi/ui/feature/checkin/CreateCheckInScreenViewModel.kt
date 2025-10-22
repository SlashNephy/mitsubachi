package blue.starry.mitsubachi.ui.feature.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.usecase.CreateCheckInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SHOUT_MAX_LENGTH = 160

@HiltViewModel
class CreateCheckInScreenViewModel @Inject constructor(
  private val createCheckInUseCase: CreateCheckInUseCase,
) : ViewModel() {
  data class ShoutState(
    val value: String,
    val remainingLength: Int,
    val hasError: Boolean,
  ) {
    val valueOrNull: String?
      get() = value.ifBlank { null }
  }

  private val _state = MutableStateFlow(
    ShoutState(value = "", remainingLength = SHOUT_MAX_LENGTH, hasError = false),
  )
  val state = _state.asStateFlow()

  fun createCheckIn(venue: Venue, shout: String? = null): Job {
    return viewModelScope.launch {
      createCheckInUseCase(venue, shout)
    }
  }

  fun onShoutUpdated(shout: String) {
    _state.value = ShoutState(
      value = shout,
      remainingLength = SHOUT_MAX_LENGTH - countShoutLength(shout),
      hasError = !validateShout(shout),
    )
  }

  private fun countShoutLength(shout: String): Int {
    return shout.codePointCount(0, shout.length)
  }

  private fun validateShout(shout: String): Boolean {
    return countShoutLength(shout) <= SHOUT_MAX_LENGTH
  }
}
