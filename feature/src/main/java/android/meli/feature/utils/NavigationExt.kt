package android.meli.feature.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import timber.log.Timber

fun Context.navigateTo(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        startActivity(intent)
    } catch (e: Exception) {
        Timber.e(e, "Unable to navigate to: $uri")
    }
}
