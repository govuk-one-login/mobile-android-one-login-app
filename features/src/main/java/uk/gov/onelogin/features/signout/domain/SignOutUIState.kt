package uk.gov.onelogin.features.signout.domain

import androidx.annotation.StringRes
import uk.gov.android.onelogin.core.R
import uk.gov.android.ui.componentsv2.button.ButtonType

@Suppress("LongParameterList")
enum class SignOutUIState(
    @StringRes val title: Int,
    @StringRes val header: Int,
    @StringRes val subTitle: Int?,
    val bullets: List<Int>,
    @StringRes val footer: Int,
    @StringRes val button: Int,
    val buttonType: ButtonType
) {
    Wallet(
        title = R.string.app_signOutConfirmationTitle,
        header = R.string.app_signOutConfirmationBody1,
        subTitle = R.string.app_signOutConfirmationSubtitle,
        bullets = listOf(
            R.string.app_signOutConfirmationBullet1,
            R.string.app_signOutConfirmationBullet2,
            R.string.app_signOutConfirmationBullet3
        ),
        footer = R.string.app_signOutConfirmationBody3,
        button = R.string.app_signOutAndDeleteAppDataButton,
        buttonType = ButtonType.Error
    ),
    NoWallet(
        title = R.string.app_signOutConfirmationTitle_no_wallet,
        header = R.string.app_signOutConfirmationBody1_no_wallet,
        subTitle = null,
        bullets = listOf(
            R.string.app_signOutConfirmationBullet1_no_wallet,
            R.string.app_signOutConfirmationBullet2_no_wallet
        ),
        footer = R.string.app_signOutConfirmationBody2_no_wallet,
        button = R.string.app_signOutButton_no_wallet,
        buttonType = ButtonType.Primary
    )
}
