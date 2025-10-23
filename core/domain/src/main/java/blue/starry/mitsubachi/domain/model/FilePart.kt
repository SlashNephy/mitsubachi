package blue.starry.mitsubachi.domain.model

data class FilePart(
  val data: ByteArray,
  val fileName: String,
  val contentType: String?,
) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as FilePart

    if (!data.contentEquals(other.data)) return false
    if (fileName != other.fileName) return false
    if (contentType != other.contentType) return false

    return true
  }

  override fun hashCode(): Int {
    var result = data.contentHashCode()
    result = 31 * result + fileName.hashCode()
    result = 31 * result + (contentType?.hashCode() ?: 0)
    return result
  }
}
