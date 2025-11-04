package blue.starry.mitsubachi.domain.usecase

import kotlinx.coroutines.flow.SharedFlow

interface DeviceLocaleRepository {
  val onLocaleChanged: SharedFlow<Unit>
}
