package blue.starry.mitsubachi.core.data.di

import android.content.Context
import androidx.room.Room
import blue.starry.mitsubachi.core.data.database.MitsubachiDatabase
import blue.starry.mitsubachi.core.data.database.security.DatabasePassphraseProvider
import blue.starry.mitsubachi.core.data.database.security.DatabasePassphraseProviderImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class EncryptedMitsubachiDatabaseModule {
    @Binds
    internal abstract fun bind(impl: DatabasePassphraseProviderImpl): DatabasePassphraseProvider

    internal companion object {
        @Provides
        @Singleton
        internal fun provide(
            @ApplicationContext context: Context,
            passphraseProvider: DatabasePassphraseProvider,
        ): MitsubachiDatabase {
            System.loadLibrary("sqlcipher")

            val passphrase = runBlocking(Dispatchers.IO) {
                passphraseProvider.getPassphrase()
            }
            val factory = SupportOpenHelperFactory(passphrase)

            return Room
                .databaseBuilder<MitsubachiDatabase>(context, name = "mitsubachi.db")
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration(dropAllTables = true)
                .build()
        }
    }
}
