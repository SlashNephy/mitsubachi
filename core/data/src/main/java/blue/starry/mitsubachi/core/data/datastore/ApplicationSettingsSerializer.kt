package blue.starry.mitsubachi.core.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object ApplicationSettingsSerializer : Serializer<ApplicationSettings> {
  override val defaultValue: ApplicationSettings = ApplicationSettings.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): ApplicationSettings {
    try {
      return ApplicationSettings.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: ApplicationSettings, output: OutputStream) {
    t.writeTo(output)
  }
}
