package blue.starry.mitsubachi.core.common

import android.annotation.SuppressLint
import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object UriSerializer : KSerializer<Uri> {
  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
    checkNotNull(this::class.qualifiedName),
    PrimitiveKind.STRING,
  )

  override fun serialize(encoder: Encoder, value: Uri) {
    encoder.encodeString(value.toString())
  }

  @SuppressLint("UseKtx")
  override fun deserialize(decoder: Decoder): Uri {
    return Uri.parse(decoder.decodeString())
  }
}
