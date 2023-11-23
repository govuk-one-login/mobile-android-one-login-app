package uk.gov.onelogin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import uk.gov.onelogin.home.HomeRoutes
import uk.gov.onelogin.login.LoginRoutes
import uk.gov.onelogin.network.auth.IAuthCodeExchange
import uk.gov.onelogin.network.auth.response.TokenResponse
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authCodeExchange: IAuthCodeExchange
) : ViewModel() {
    private val _next = MutableLiveData<String>()

    val next: LiveData<String> = _next

    private val tag = this::class.java.simpleName

    fun handleIntent(
        data: Uri?,
        context: Context
    ) {
        when {
            data != null -> handleIntentData(data, context)
            tokensAvailable(context) -> _next.value = HomeRoutes.START
            else -> _next.value = LoginRoutes.START
        }
    }

    private fun handleIntentData(data: Uri, context: Context) {
        val webBaseHost = context.resources.getString(R.string.webBaseHost)
        val host = data.host.toString()

        if (host.equals(webBaseHost)) {
            val code = data.getQueryParameter(AUTH_CODE_PARAMETER)

            if (!code.isNullOrEmpty()) {
                exchangeCode(code, context) { successful ->
                    if (successful) {
                        _next.value = HomeRoutes.START
                    }
                }
            }
        }
    }

    private fun exchangeCode(
        code: String,
        context: Context,
        block: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tokens = authCodeExchange.exchangeCode(code = code)

                storeTokens(tokens, context)

                block(true)
            } catch (e: Exception) {
                println("$tag - Caught exception - $e")
            } catch (e: Error) {
                println("$tag - Caught error - $e")
            }

            block(false)
        }
    }

    private fun storeTokens(
        tokens: TokenResponse,
        context: Context
    ) {
        with(
            context.getSharedPreferences(
                TOKENS_PREFERENCES_FILE,
                Context.MODE_PRIVATE
            ).edit()
        ) {
            putString(TOKENS_PREFERENCES_KEY, Gson().toJson(tokens))
            apply()
        }
    }

    private fun tokensAvailable(context: Context): Boolean {
        return context.getSharedPreferences(
            TOKENS_PREFERENCES_FILE,
            Context.MODE_PRIVATE
        ).getString(TOKENS_PREFERENCES_KEY, null) != null
    }

    companion object {
        private const val AUTH_CODE_PARAMETER = "code"
        const val TOKENS_PREFERENCES_FILE = "tokens"
        const val TOKENS_PREFERENCES_KEY = "authTokens"
    }
}
