package android.meli.core.data.network.articles

import kotlinx.serialization.Serializable

@Serializable
data class ArticleResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<ArticleNetwork>
)

@Serializable
data class ArticleNetwork(
    val id: Int,
    val title: String,
    val summary: String,
    val image_url: String,
    val published_at: String,
    val updated_at: String,
    val url: String,
    val authors: List<AuthorNetwork>
)

@Serializable
data class AuthorNetwork(
    val name: String,
)