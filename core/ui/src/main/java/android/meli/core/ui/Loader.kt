package android.meli.core.ui

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

/**
 * A composable function that displays a loading indicator centered within a BoxScope.
 */
@Composable
fun BoxScope.Loader() {
    CircularProgressIndicator(modifier = Modifier
        .align(Alignment.Center)
        .testTag("loading_indicator")
    )
}
