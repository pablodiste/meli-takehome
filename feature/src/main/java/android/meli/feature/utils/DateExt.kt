package android.meli.feature.utils

import android.meli.core.domain.model.Article

val isoDateFormat by lazy { java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", java.util.Locale.getDefault()) }
val dateFormat by lazy { java.text.SimpleDateFormat("dd MMMM yyyy", java.util.Locale.getDefault()) }

// Format date as Day Month Year
fun Article.updatedAtFormatted(): String {
    return try {
        val date = isoDateFormat.parse(updatedAt)
        date?.let { dateFormat.format(it) } ?: ""
    } catch (e: Exception) {
        ""
    }
}