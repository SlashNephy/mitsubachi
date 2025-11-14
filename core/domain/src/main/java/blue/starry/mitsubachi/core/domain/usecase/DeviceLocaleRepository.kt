package blue.starry.mitsubachi.core.domain.usecase

import kotlinx.coroutines.flow.SharedFlow

interface DeviceLocaleRepository {
  val onLocaleChanged: SharedFlow<Unit>
}
