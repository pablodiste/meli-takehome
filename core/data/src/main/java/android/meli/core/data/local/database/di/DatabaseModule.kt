package android.meli.core.data.local.database.di

import android.content.Context
import android.meli.core.data.local.database.AppDatabase
import android.meli.core.data.local.database.ArticleDao
import android.meli.core.data.local.database.ArticleDatabaseDataSource
import android.meli.core.data.local.database.DefaultArticleDatabaseDataSource
import androidx.room.Room
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    abstract fun provideArticleDataSource(articleDataSource: DefaultArticleDatabaseDataSource): ArticleDatabaseDataSource

    companion object {
        @Provides
        fun provideArticleDao(appDatabase: AppDatabase): ArticleDao {
            return appDatabase.articleDao()
        }

        @Provides
        @Singleton
        fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
            return Room.databaseBuilder(
                appContext,
                AppDatabase::class.java,
                "TestApplication"
            ).fallbackToDestructiveMigration(dropAllTables = true).build()
        }
    }

}
