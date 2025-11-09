package blue.starry.mitsubachi.core.ui.widget.state

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.dataStoreFile
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.KSerializer
import java.io.File

class WidgetStateDefinition<S : WidgetState>(
  private val dataStorePathPrefix: String,
  private val defaultValue: S,
  private val kSerializer: KSerializer<S>,
) : GlanceStateDefinition<S> {
  override fun getLocation(context: Context, fileKey: String): File {
    return context.dataStoreFile("${dataStorePathPrefix}_$fileKey")
  }

  override suspend fun getDataStore(context: Context, fileKey: String): DataStore<S> {
    return DataStoreFactory.create(
      serializer = WidgetStateSerializer(defaultValue, kSerializer),
      corruptionHandler = ReplaceFileCorruptionHandler { defaultValue },
      produceFile = { getLocation(context, fileKey) },
    )
  }
}
