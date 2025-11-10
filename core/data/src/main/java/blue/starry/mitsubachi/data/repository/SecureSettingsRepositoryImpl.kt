package blue.starry.mitsubachi.data.repository

import blue.starry.mitsubachi.data.database.EncryptedAppDatabase
import blue.starry.mitsubachi.domain.usecase.SecureSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureSettingsRepositoryImpl @Inject constructor(
  private val database: EncryptedAppDatabase,
) : SecureSettingsRepository
