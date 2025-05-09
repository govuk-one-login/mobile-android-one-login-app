package uk.gov.onelogin.core.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import uk.gov.android.ui.theme.m3.DarkColorPalette
import uk.gov.android.ui.theme.m3.LightColorPalette
import uk.gov.android.ui.theme.m3.Shapes
import uk.gov.android.ui.theme.m3.Typography

/**
 * A temporary wrapper for [MaterialTheme] until [GdsThemeV2] is complete
 *
 * [GdsTheme] is not compliant with edge to edge requirements
 *
 */
@Suppress("DEPRECATION")
@Composable
fun GdsThemeE2E(
    darkTheme: Boolean = isSystemInDarkTheme(),
    shapes: Shapes = Shapes,
    typography: Typography = Typography,
    content: @Composable () -> Unit,
) {
    val colors = if (darkTheme) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colorScheme = colors,
        shapes = shapes,
        typography = typography,
    ) {
        val view = LocalView.current
        if (!view.isInEditMode && view.context is Activity) {
            SideEffect {
                val window = (view.context as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
            }
        }

        content()
    }
}