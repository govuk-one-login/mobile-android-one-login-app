package uk.gov.onelogin.ui.main

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import uk.gov.onelogin.R

sealed class BottomNavDestination(
    val key: String,
    @StringRes
    val label: Int,
    @DrawableRes
    val icon: Int
) {
    data object Home : BottomNavDestination(
        key = "home",
        label = R.string.app_home,
        icon = R.drawable.outline_home_24
    )
    data object Wallet : BottomNavDestination(
        key = "wallet",
        label = R.string.app_wallet,
        icon = R.drawable.outline_article_24
    )
    data object Profile : BottomNavDestination(
        key = "profile",
        label = R.string.app_profile,
        icon = R.drawable.baseline_account_circle_24
    )
}
