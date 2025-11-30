package blue.starry.mitsubachi.core.common

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object ZonedDateTimeSerializer : KSerializer<ZonedDateTime> {
  private val formatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME

  override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
    checkNotNull(this::class.qualifiedName),
    PrimitiveKind.STRING,
  )

  override fun serialize(encoder: Encoder, value: ZonedDateTime) {
    encoder.encodeString(value.format(formatter))
  }

  override fun deserialize(decoder: Decoder): ZonedDateTime {
    val string = decoder.decodeString()
    return ZonedDateTime.parse(string, formatter)
  }
}
