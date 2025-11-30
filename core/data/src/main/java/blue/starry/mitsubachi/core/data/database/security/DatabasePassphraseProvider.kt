package blue.starry.mitsubachi.core.data.database.security

interface DatabasePassphraseProvider {
  suspend fun getPassphrase(): ByteArray
}
