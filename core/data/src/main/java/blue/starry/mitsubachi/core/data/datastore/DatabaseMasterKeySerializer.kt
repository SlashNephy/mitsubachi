package blue.starry.mitsubachi.core.data.datastore

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import com.google.protobuf.InvalidProtocolBufferException
import java.io.InputStream
import java.io.OutputStream

object DatabaseMasterKeySerializer : Serializer<DatabaseMasterKey> {
  override val defaultValue: DatabaseMasterKey = DatabaseMasterKey.getDefaultInstance()

  override suspend fun readFrom(input: InputStream): DatabaseMasterKey {
    try {
      return DatabaseMasterKey.parseFrom(input)
    } catch (exception: InvalidProtocolBufferException) {
      throw CorruptionException("cannot read proto.", exception)
    }
  }

  override suspend fun writeTo(t: DatabaseMasterKey, output: OutputStream) {
    t.writeTo(output)
  }
}
