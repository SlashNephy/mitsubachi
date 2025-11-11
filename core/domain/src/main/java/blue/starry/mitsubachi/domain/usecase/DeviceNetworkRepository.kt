package blue.starry.mitsubachi.domain.usecase

interface DeviceNetworkRepository {
  fun isDataSaverEnabled(): Boolean
}
