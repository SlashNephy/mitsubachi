package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.dao.UserSettingsDao
import blue.starry.mitsubachi.core.domain.usecase.SecureUserSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureUserSettingsRepositoryImpl @Inject constructor(
  private val dao: UserSettingsDao,
) : SecureUserSettingsRepository
