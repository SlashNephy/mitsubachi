package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.EncryptedAppDatabase
import blue.starry.mitsubachi.core.domain.usecase.SecureSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureSettingsRepositoryImpl @Inject constructor(
  private val database: EncryptedAppDatabase,
) : SecureSettingsRepository
