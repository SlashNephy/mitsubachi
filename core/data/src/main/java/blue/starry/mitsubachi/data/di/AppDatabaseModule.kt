package blue.starry.mitsubachi.data.di

import android.content.Context
import androidx.room.Room
import blue.starry.mitsubachi.data.database.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppDatabaseModule {
  @Provides
  @Singleton
  fun provide(@ApplicationContext context: Context): AppDatabase {
    return Room
      .databaseBuilder(
        context,
        AppDatabase::class.java,
        "mitsubachi-database",
      )
      .build()
  }
}
