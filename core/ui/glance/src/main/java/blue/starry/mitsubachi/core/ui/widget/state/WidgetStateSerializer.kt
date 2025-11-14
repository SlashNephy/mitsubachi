package blue.starry.mitsubachi.core.ui.widget.state

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import java.io.InputStream
import java.io.OutputStream

@OptIn(ExperimentalSerializationApi::class)
internal class WidgetStateSerializer<S : WidgetState>(
  override val defaultValue: S,
  private val kSerializer: KSerializer<S>,
) : Serializer<S> {
  private val json = Json {
    ignoreUnknownKeys = true
  }

  override suspend fun readFrom(input: InputStream): S {
    return try {
      json.decodeFromStream(kSerializer, input)
    } catch (e: SerializationException) {
      throw CorruptionException("Cannot read widget state", e)
    }
  }

  override suspend fun writeTo(t: S, output: OutputStream) {
    json.encodeToStream(kSerializer, t, output)
  }
}
