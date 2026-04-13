package uk.gov.onelogin.features.test.login.domain.signin.locallogin

import androidx.fragment.app.FragmentActivity
import uk.gov.onelogin.core.tokens.data.LocalAuthStatus

fun interface HandleLocalLogin {
    suspend operator fun invoke(
        fragmentActivity: FragmentActivity,
        callback: (LocalAuthStatus) -> Unit,
    )
}
