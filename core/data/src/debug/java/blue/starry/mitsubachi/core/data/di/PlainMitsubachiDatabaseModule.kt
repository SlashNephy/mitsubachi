package blue.starry.mitsubachi.core.data.di

import android.content.Context
import androidx.room.Room
import blue.starry.mitsubachi.core.data.database.MitsubachiDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object PlainMitsubachiDatabaseModule {
  @Provides
  @Singleton
  internal fun provide(@ApplicationContext context: Context): MitsubachiDatabase {
    // デバッグビルドでは利便性のためデータベースを暗号化しない
    return Room
      .databaseBuilder<MitsubachiDatabase>(context, name = "mitsubachi_debug.db")
      .fallbackToDestructiveMigration(dropAllTables = true)
      .build()
  }
}
