package uk.gov.onelogin.features.login.ui.signin.splash.theme

import androidx.compose.ui.graphics.Color
import uk.gov.android.ui.theme.m3.CustomColorsScheme
import uk.gov.android.ui.theme.m3.DarkColorPaletteV2

internal val SplashThemeColorScheme = with(SplashThemeColors) {
    DarkColorPaletteV2.copy(
        primary = Primary,
        onPrimary = OnPrimary,
        surface = Surface,
        onSurface = OnSurface,
    )
}

internal val SplashThemeExtendedColorScheme = with(SplashThemeExtendedColors) {
    CustomColorsScheme(
        buttonShadow = ButtonShadow,
        focusButtonHighlighted = FocusButtonHighlighted,
        focusState = FocusState,
        focusStateContent = FocusStateContent,
        focusStateShadow = FocusStateShadow,
        primaryButtonHighlighted = PrimaryButtonHighlighted,
        spinnerIcon = SpinnerIcon,
    )
}

private object SplashThemeColors {
    val Primary = SplashThemeColorTokens.White
    val OnPrimary = SplashThemeColorTokens.Black1
    val Surface = SplashThemeColorTokens.Blue2
    val OnSurface = SplashThemeColorTokens.White
}

private object SplashThemeExtendedColors {
    val ButtonShadow = SplashThemeColorTokens.Grey9
    val PrimaryButtonHighlighted = SplashThemeColorTokens.Grey8
    val SpinnerIcon = SplashThemeColorTokens.White
    val FocusState = SplashThemeColorTokens.Yellow1
    val FocusStateContent = SplashThemeColorTokens.Black1
    val FocusStateShadow = SplashThemeColorTokens.Yellow2
    val FocusButtonHighlighted = SplashThemeColorTokens.Yellow4
}

private object SplashThemeColorTokens {
    val Black1 = Color(0xFF0B0C0C)
    val Blue2 = Color(0xFF1D70B8)
    val Grey8 = Color(0xFFCECECE)
    val Grey9 = Color(0xFF666666)
    val White = Color(0xFFFFFFFF)
    val Yellow1 = Color(0xFFFFDD00)
    val Yellow2 = Color(0xFF665800)
    val Yellow4 = Color(0xFFBFA600)
}
