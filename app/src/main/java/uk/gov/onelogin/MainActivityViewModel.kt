package uk.gov.onelogin

import android.content.Context
import android.content.Intent
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.android.authentication.LoginSession
import uk.gov.android.authentication.TokenResponse
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.network.auth.IAuthCodeExchange
import uk.gov.onelogin.ui.home.HomeRoutes
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    val appRoutes: IAppRoutes,
    private val authCodeExchange: IAuthCodeExchange,
    private val loginSession: LoginSession
) : ViewModel() {
    private val tag = this::class.java.simpleName

    private val _next = MutableLiveData<String>()
    val next: LiveData<String> = _next

    fun handleIntent(
        intent: Intent?,
        context: Context
    ) {
        val data = intent?.data
        when {
            data != null -> handleActivityResult(context, intent)
            tokensAvailable(context) -> _next.value = HomeRoutes.START
            else -> _next.value = LoginRoutes.START
        }
    }

    private fun storeTokens(
        context: Context,
        tokens: TokenResponse
    ) {
        with(
            context.getSharedPreferences(
                TOKENS_PREFERENCES_FILE,
                Context.MODE_PRIVATE
            ).edit()
        ) {
            putString(TOKENS_PREFERENCES_KEY, tokens.jsonSerializeString())
            apply()
        }
    }

    private fun tokensAvailable(context: Context): Boolean {
        return context.getSharedPreferences(
            TOKENS_PREFERENCES_FILE,
            Context.MODE_PRIVATE
        ).getString(TOKENS_PREFERENCES_KEY, null) != null
    }

    private fun handleActivityResult(context: Context, intent: Intent) {
        try {
            loginSession.finalise(intent = intent) { tokens ->
                storeTokens(
                    context = context,
                    tokens = tokens
                )

                _next.value = HomeRoutes.START
            }
        } catch (e: Error) {
            _next.value = LoginRoutes.START
        }
    }

    companion object {
        private const val AUTH_CODE_PARAMETER = "code"
        const val TOKENS_PREFERENCES_FILE = "tokens"
        const val TOKENS_PREFERENCES_KEY = "authTokens"
    }
}
