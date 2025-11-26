package blue.starry.mitsubachi.core.data.repository

import blue.starry.mitsubachi.core.data.database.dao.SettingsDao
import blue.starry.mitsubachi.core.domain.usecase.SecureSettingsRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureSettingsRepositoryImpl @Inject constructor(
    private val dao: SettingsDao,
) : SecureSettingsRepository
