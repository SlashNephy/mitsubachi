package blue.starry.mitsubachi.core.domain.usecase

interface DeviceNetworkRepository {
  fun isDataSaverEnabled(): Boolean
}
