package uk.gov.onelogin.features.login.ui.signin.splash.theme

import androidx.compose.runtime.Composable
import uk.gov.android.ui.theme.m3.CustomColorsScheme
import uk.gov.android.ui.theme.m3.DarkColorPaletteV2
import uk.gov.android.ui.theme.m3.GdsTheme
import uk.gov.android.ui.theme.m3.GdsThemeDefaults

@Composable
fun SplashTheme(
    content: @Composable () -> Unit,
) = GdsTheme(
    colorScheme = SplashThemeColorScheme,
    extendedColorScheme = SplashThemeExtendedColorScheme,
    content = content
)
