package android.meli.core.ui
import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

/**
 * MultiPreview annotation for displaying a composable on various common device sizes.
 */
@Preview(name = "Phone", device = "id:pixel_5", showBackground = true)
@Preview(name = "Phone Landscape", device = "spec:width=411dp,height=891dp,dpi=420,orientation=landscape", showBackground = true)
@Preview(name = "Tablet", device = "id:pixel_tablet", showBackground = true)
@Preview(name = "Foldable Open", device = "spec:parent=pixel_fold,orientation=landscape", showBackground = true)
annotation class DevicePreviews

/**
 * MultiPreview annotation for displaying a composable in Light and Dark themes.
 */
@Preview(name = "Light Theme", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Preview(name = "Dark Theme", showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
annotation class ThemePreviews

/**
 * MultiPreview annotation for different font scaling.
 */
@Preview(name = "Default Font Size", group = "Font Scales", showBackground = true, fontScale = 1.0f)
@Preview(name = "Large Font Size", group = "Font Scales", showBackground = true, fontScale = 1.5f)
@Preview(name = "Small Font Size", group = "Font Scales", showBackground = true, fontScale = 0.85f)
annotation class FontScalePreviews

/**
 * Combine multiple preview configurations.
 * For example, preview on different devices AND in light/dark themes.
 */
@DevicePreviews
@ThemePreviews
annotation class CombinedPreviews
