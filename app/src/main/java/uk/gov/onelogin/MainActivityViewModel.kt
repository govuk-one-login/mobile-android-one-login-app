package uk.gov.onelogin

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import uk.gov.onelogin.network.auth.IAuthCodeExchange
import uk.gov.onelogin.network.auth.response.TokenResponse
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val authCodeExchange: IAuthCodeExchange,
    @ApplicationContext
    private val context: Context
): ViewModel() {
    private val _isLoading = MutableLiveData(true)

    val isLoading: LiveData<Boolean> = _isLoading

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
                    exchangeCode(code)
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
        code: String
    ) {
        viewModelScope.launch {
            try {
                val tokens = authCodeExchange.exchangeCode(code = code)

                storeTokens(tokens)
            } catch (e: Exception) {
                println("$tag - Caught exception - $e")
            } catch (e: Error) {
                println("$tag - Caught error - $e")
            }
        }
    }

    private fun storeTokens(
        tokens: TokenResponse
    ) {
        with (
            context.getSharedPreferences(
                TOKENS_PREFERENCES_FILE,
                Context.MODE_PRIVATE
            ).edit()
        ) {
            putString(TOKENS_PREFERENCES_KEY, Gson().toJson(tokens))
            apply()
        }
    }

    companion object {
        private const val AUTH_CODE_PARAMETER = "code"
        private const val TOKENS_PREFERENCES_FILE = "tokens"
        private const val TOKENS_PREFERENCES_KEY = "authTokens"
    }
}
