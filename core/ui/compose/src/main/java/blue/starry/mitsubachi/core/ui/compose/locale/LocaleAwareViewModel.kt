package blue.starry.mitsubachi.core.ui.compose.locale

import androidx.lifecycle.ViewModel
import blue.starry.mitsubachi.core.domain.usecase.DeviceLocaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LocaleAwareViewModel @Inject constructor(
  repository: DeviceLocaleRepository,
) : ViewModel(), DeviceLocaleRepository by repository
