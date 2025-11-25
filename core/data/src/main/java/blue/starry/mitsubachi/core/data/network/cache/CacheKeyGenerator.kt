package blue.starry.mitsubachi.core.data.network.cache

import java.security.MessageDigest

internal object CacheKeyGenerator {
  fun key(vararg keys: String): String {
    check(keys.isNotEmpty())

    val base = keys.joinToString("_").toByteArray()
    val sha512 = MessageDigest.getInstance("SHA-512")
    return sha512.digest(base).toHexString()
  }
}
