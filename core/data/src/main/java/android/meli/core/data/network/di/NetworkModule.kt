package android.meli.core.data.network.di

import android.meli.core.data.network.RetrofitClient
import android.meli.core.data.network.articles.ArticleNetworkDataSource
import android.meli.core.data.network.articles.ArticlesService
import android.meli.core.data.network.articles.DefaultArticleNetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class NetworkModule {

    @Binds
    abstract fun provideArticleNetworkDataSource(articleNetworkDataSource: DefaultArticleNetworkDataSource): ArticleNetworkDataSource

    companion object {
        @Provides
        @Singleton
        fun provideArticlesService(retrofitClient: RetrofitClient): ArticlesService {
            return retrofitClient.articlesService
        }

        @Provides
        @Singleton
        fun provideRetrofitClient(): RetrofitClient {
            return RetrofitClient()
        }

    }
}
