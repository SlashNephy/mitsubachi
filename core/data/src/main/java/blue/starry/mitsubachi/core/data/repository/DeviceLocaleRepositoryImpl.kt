package blue.starry.mitsubachi.core.data.repository

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import blue.starry.mitsubachi.core.domain.ApplicationScope
import blue.starry.mitsubachi.core.domain.usecase.DeviceLocaleRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class DeviceLocaleRepositoryImpl @Inject constructor(
  @param:ApplicationContext private val context: Context,
  @param:ApplicationScope private val coroutineScope: CoroutineScope,
) : DeviceLocaleRepository {
  private val _onLocaleChanged = MutableSharedFlow<Unit>()
  override val onLocaleChanged = _onLocaleChanged.asSharedFlow()

  init {
    context.registerReceiver(
      object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
          coroutineScope.launch {
            _onLocaleChanged.emit(Unit)
          }
        }
      },
      IntentFilter(Intent.ACTION_LOCALE_CHANGED),
      Context.RECEIVER_NOT_EXPORTED,
    )
  }
}
