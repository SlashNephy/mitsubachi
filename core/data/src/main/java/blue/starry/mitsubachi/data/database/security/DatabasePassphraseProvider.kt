package blue.starry.mitsubachi.data.database.security

interface DatabasePassphraseProvider {
  suspend fun getPassphrase(): ByteArray
}
