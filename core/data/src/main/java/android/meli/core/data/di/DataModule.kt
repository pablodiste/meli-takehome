package android.meli.core.data.di

import android.meli.core.data.repositories.DefaultArticlesRepository
import android.meli.core.domain.repositories.ArticlesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsArticlesRepository(articlesRepository: DefaultArticlesRepository): ArticlesRepository
}
