package android.meli.core.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays an empty state message centered within a BoxScope.
 *
 * @param text The message to display when there is no content.
 */
@Composable
fun BoxScope.EmptyState(text: String) {
    Text(
        text = text,
        modifier = Modifier.align(Alignment.Center).padding(32.dp),
        style = MaterialTheme.typography.bodyLarge
    )
}
