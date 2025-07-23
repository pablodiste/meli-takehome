package android.meli.core.data.mappers

import android.meli.core.data.local.database.ArticleEntity
import android.meli.core.data.network.articles.ArticleNetwork
import android.meli.core.domain.model.Article

fun ArticleEntity.toDomain(): Article {
    return Article(
        id = id,
        title = title,
        summary = summary,
        imageUrl = imageUrl,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
        url = url,
        authors = authors,
    )
}

fun ArticleNetwork.toEntity(query: String): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        summary = summary,
        imageUrl = image_url,
        publishedAt = published_at,
        updatedAt = updated_at,
        searchQuery = query,
        url = url,
        authors = authors.joinToString(", ") { it.name }
    )
}