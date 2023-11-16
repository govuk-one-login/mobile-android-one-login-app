package uk.gov.onelogin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import kotlinx.coroutines.launch
import uk.gov.onelogin.home.HomeRoutes
import uk.gov.onelogin.network.auth.IAuthCodeExchange
import uk.gov.onelogin.network.auth.response.TokenResponse

class MainActivityViewModel constructor(
    private val authCodeExchange: IAuthCodeExchange,
    private val context: Context
) : ViewModel() {
    private val _isLoading = MutableLiveData(true)

    val isLoading: LiveData<Boolean> = _isLoading

    private val _next = MutableLiveData<String>()

    val next: LiveData<String> = _next

    private val tag = this::class.java.simpleName

    fun handleDeepLink(
        data: Uri?
    ) {
        if (data != null) {
            val webBaseHost = context.resources.getString(R.string.webBaseHost)
            val host = data.host.toString()

            if (host.equals(webBaseHost)) {
                val code = data.getQueryParameter(AUTH_CODE_PARAMETER)

                if (!code.isNullOrEmpty()) {
                    exchangeCode(code) { successful ->
                        if (successful) {
                            _next.value = HomeRoutes.START
                        }
                    }
                }
            }
        } else {
            // Pause to fully show the splash screen
            viewModelScope.launch {
                Thread.sleep(1000)
            }
        }

        _isLoading.value = false
    }

    private fun exchangeCode(
        code: String,
        block: (success: Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val tokens = authCodeExchange.exchangeCode(code = code)

                storeTokens(tokens)

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
        tokens: TokenResponse
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
        context.getSharedPreferences(
            TOKENS_PREFERENCES_FILE,
            Context.MODE_PRIVATE
        ).getString(TOKENS_PREFERENCES_KEY, null)?.let {
            val tokens = Gson().fromJson(
                it,
                TokenResponse::class.java
            )
        }
    }

    companion object {
        private const val AUTH_CODE_PARAMETER = "code"
        const val TOKENS_PREFERENCES_FILE = "tokens"
        const val TOKENS_PREFERENCES_KEY = "authTokens"

        class Factory(
            private val authCodeExchange: IAuthCodeExchange,
            private val context: Context
        ) : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainActivityViewModel(authCodeExchange, context) as T
            }
        }
    }
}
