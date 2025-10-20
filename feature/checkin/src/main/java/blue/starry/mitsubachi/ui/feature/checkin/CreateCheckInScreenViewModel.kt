package blue.starry.mitsubachi.ui.feature.checkin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import blue.starry.mitsubachi.domain.model.Venue
import blue.starry.mitsubachi.domain.usecase.CreateCheckInUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateCheckInScreenViewModel @Inject constructor(
  private val createCheckInUseCase: CreateCheckInUseCase,
) : ViewModel() {
  fun createCheckIn(venue: Venue, shout: String? = null): Job {
    return viewModelScope.launch {
      createCheckInUseCase(venue, shout)
    }
  }
}
