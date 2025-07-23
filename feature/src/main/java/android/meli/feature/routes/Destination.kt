package android.meli.feature.routes

import kotlinx.serialization.Serializable

@Serializable
sealed class Destination(val route: String) {
    @Serializable
    object ArticleList : Destination("articles")
    @Serializable
    data class ArticleDetails(val id: Int) : Destination("article-details/$id")
}