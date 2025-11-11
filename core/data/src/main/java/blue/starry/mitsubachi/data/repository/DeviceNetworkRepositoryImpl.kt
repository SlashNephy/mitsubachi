package blue.starry.mitsubachi.data.repository

import android.net.ConnectivityManager
import blue.starry.mitsubachi.domain.usecase.DeviceNetworkRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeviceNetworkRepositoryImpl @Inject constructor(
  private val connectivityManager: ConnectivityManager,
) : DeviceNetworkRepository {
  override fun isDataSaverEnabled(): Boolean {
    return connectivityManager.restrictBackgroundStatus == ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
  }
}
