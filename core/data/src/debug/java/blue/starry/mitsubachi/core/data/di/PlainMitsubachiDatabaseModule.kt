package blue.starry.mitsubachi.core.data.di

import android.content.Context
import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
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
      // ドライバを指定しない場合、Room は framework SQLite への互換シムを使う。
      // framework SQLite は結果を CursorWindow (約 2MB) に載せて返すため、
      // それを超える行があると読み出しだけが SQLiteBlobTooBigException で失敗する。
      // release ビルドの SQLCipher は 16MB の CursorWindow を持つのでこの問題が出ず、
      // デバッグビルドだけが壊れる状態になっていた。
      .setDriver(BundledSQLiteDriver())
      .fallbackToDestructiveMigration(dropAllTables = true)
      .build()
  }
}
