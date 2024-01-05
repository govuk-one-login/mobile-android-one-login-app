package uk.gov.android.authentication

import android.content.Context
import android.content.Intent
import net.openid.appauth.TokenResponse

interface ILoginSession {
    fun present(
        configuration: LoginSessionConfiguration
    )

    fun init(context: Context): ILoginSession
    fun finalise(intent: Intent, callback: (tokens: TokenResponse) -> Unit)
}
