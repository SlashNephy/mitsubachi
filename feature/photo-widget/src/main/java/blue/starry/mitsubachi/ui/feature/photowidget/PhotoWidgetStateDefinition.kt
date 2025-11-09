package blue.starry.mitsubachi.ui.feature.photowidget

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import androidx.glance.state.GlanceStateDefinition
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.io.OutputStream

/**
 * GlanceStateDefinition for PhotoWidget using kotlinx-serialization
 */
object PhotoWidgetStateDefinition : GlanceStateDefinition<PhotoWidgetState> {
  private const val DATA_STORE_FILENAME = "photo_widget_state"

  private val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
  }

  private val Context.dataStore by dataStore(
    fileName = DATA_STORE_FILENAME,
    serializer = PhotoWidgetStateSerializer,
  )

  override suspend fun getDataStore(
    context: Context,
    fileKey: String,
  ): DataStore<PhotoWidgetState> {
    return context.dataStore
  }

  override fun getLocation(context: Context, fileKey: String): File {
    return File(context.filesDir, "datastore/$DATA_STORE_FILENAME")
  }

  object PhotoWidgetStateSerializer : Serializer<PhotoWidgetState> {
    override val defaultValue: PhotoWidgetState
      get() = PhotoWidgetState()

    override suspend fun readFrom(input: InputStream): PhotoWidgetState {
      return try {
        json.decodeFromString(
          PhotoWidgetState.serializer(),
          input.readBytes().decodeToString(),
        )
      } catch (e: SerializationException) {
        throw CorruptionException("Cannot read photo widget state", e)
      }
    }

    override suspend fun writeTo(t: PhotoWidgetState, output: OutputStream) {
      output.write(
        json.encodeToString(PhotoWidgetState.serializer(), t).encodeToByteArray(),
      )
    }
  }
}
