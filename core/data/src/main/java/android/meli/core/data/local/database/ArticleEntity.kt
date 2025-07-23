package android.meli.core.data.local.database

import android.meli.core.domain.model.Article
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val publishedAt: String,
    val updatedAt: String,
    val url: String,
    val authors: String,
    val searchQuery: String
)

@Dao
interface ArticleDao {
    @Query("SELECT * FROM articles WHERE searchQuery = :query order by updatedAt DESC")
    fun getArticlesForQuery(query: String): PagingSource<Int, ArticleEntity>

    @Query("SELECT count(*) FROM articles WHERE searchQuery = :query order by updatedAt DESC")
    suspend fun getArticleCountForQuery(query: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("DELETE FROM articles WHERE searchQuery = :query")
    suspend fun clearArticlesForQuery(query: String)

    @Query("SELECT * FROM articles WHERE id = :id")
    fun getArticle(id: Int): ArticleEntity?
}