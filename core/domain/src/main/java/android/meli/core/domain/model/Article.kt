package android.meli.core.domain.model

data class Article(
    val id: Int,
    val title: String,
    val summary: String,
    val imageUrl: String,
    val publishedAt: String,
    val updatedAt: String,
    val url: String,
    val authors: String,
)