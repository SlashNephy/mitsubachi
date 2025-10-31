package blue.starry.mitsubachi.data.network

import java.security.SecureRandom
import java.util.Base64

internal fun generateRandomString(length: Int): String {
  check(length > 0)

  val random = SecureRandom()
  val bytes = ByteArray(length)
  random.nextBytes(bytes)

  return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)
}
