package blue.starry.mitsubachi.core.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object UserSettingsMapSerializer : Serializer<UserSettingsMap> {
  override val defaultValue: UserSettingsMap = UserSettingsMap.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): UserSettingsMap {
    try {
      return UserSettingsMap.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: UserSettingsMap, output: OutputStream) {
    t.writeTo(output)
  }
}
