package blue.starry.mitsubachi.data.database.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import blue.starry.mitsubachi.data.datastore.DatabaseMasterKey
import blue.starry.mitsubachi.data.repository.DatabaseMasterKeyRepository
import com.google.protobuf.kotlin.toByteString
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject

internal class DatabasePassphraseProviderImpl @Inject constructor(
  private val databaseMasterKeyRepository: DatabaseMasterKeyRepository,
) : DatabasePassphraseProvider {
  private companion object {
    private const val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
    private const val KEYSTORE_ALIAS = "database_master_key"
  }

  override suspend fun getPassphrase(): ByteArray {
    val encryptedMasterKey = databaseMasterKeyRepository.get()?.key?.toByteArray()

    // すでにマスターキーが生成済み
    if (encryptedMasterKey != null && encryptedMasterKey.isNotEmpty()) {
      return decrypt(encryptedMasterKey)
    }

    // マスターキーを生成
    val newMasterKey = generatePlainMasterKey()
    val newEncryptedMasterKey = encrypt(newMasterKey)
    databaseMasterKeyRepository.set(
      DatabaseMasterKey.newBuilder().setKey(newEncryptedMasterKey.toByteString()).build()
    )

    return newMasterKey
  }

  private fun getOrCreateSecretKey(): SecretKey {
    val keyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
    keyStore.load(null)

    val keyEntry = keyStore.getEntry(KEYSTORE_ALIAS, null) as? KeyStore.SecretKeyEntry
    return keyEntry?.secretKey ?: generateSecretKey()
  }

  private fun generateSecretKey(): SecretKey {
    val generator =
      KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE_PROVIDER)
    val parameterSpec = KeyGenParameterSpec
      .Builder(
        KEYSTORE_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
      )
      .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
      .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
      .setKeySize(256)
      .build()
    generator.init(parameterSpec)

    return generator.generateKey()
  }

  private fun encrypt(plainText: ByteArray): ByteArray {
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
    val cipherText = cipher.doFinal(plainText)

    return cipher.iv + cipherText
  }

  private fun decrypt(encryptedText: ByteArray): ByteArray {
    val ivSize = 12
    val spec = GCMParameterSpec(128, encryptedText, 0, ivSize)
    val cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.DECRYPT_MODE, getOrCreateSecretKey(), spec)

    return cipher.doFinal(encryptedText, ivSize, encryptedText.size - ivSize)
  }

  private fun generatePlainMasterKey(): ByteArray {
    val random = SecureRandom.getInstanceStrong()
    val masterKey = ByteArray(32) // 256-bit
    random.nextBytes(masterKey)

    return masterKey
  }
}
