package uk.gov.onelogin.features.developer.ui.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.util.date.getTimeMillis
import java.lang.System.currentTimeMillis
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.format
import kotlinx.datetime.format.DateTimeComponents
import kotlinx.datetime.format.char
import uk.gov.onelogin.core.tokens.domain.retrieve.GetPersistentId
import uk.gov.onelogin.core.tokens.domain.retrieve.GetTokenExpiry
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.ExpiryInfo
import uk.gov.onelogin.core.tokens.domain.save.tokenexpiry.SaveTokenExpiry
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.ACCESS_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.RefreshToken

@HiltViewModel
class TokenTabScreenViewModel @Inject constructor(
    private val saveTokenExpiry: SaveTokenExpiry,
    private val saveToOpenSecureStore: SaveToOpenSecureStore,
    private val getPersistentId: GetPersistentId,
    @param:AccessToken
    private val getAccessTokenExpiry: GetTokenExpiry,
    @param:RefreshToken
    private val getRefreshTokenExpiry: GetTokenExpiry
) : ViewModel() {
    private val _persistentId = MutableStateFlow("")
    val persistentId: StateFlow<String>
        get() = _persistentId.asStateFlow()

    init {
        viewModelScope.launch {
            setPersistentId()
        }
    }

    @OptIn(ExperimentalTime::class)
    fun getAccessTokenExp(): String {
        val epochExp = getAccessTokenExpiry()
        println("Access EXP $epochExp")
        return epochExp?.let {
            val date = Instant.fromEpochMilliseconds(it)
            formatDateInstate(date)
        } ?: NO_ACCESS_TOKEN_EXP
    }

    fun resetAccessToken() {
        saveTokenExpiry.saveExp(
            ExpiryInfo(
                key = ACCESS_TOKEN_EXPIRY_KEY,
                value = currentTimeMillis() - 1
            )
        )
    }

    fun setAccessTokenExpireTo30Seconds() {
        saveTokenExpiry.saveExp(
            ExpiryInfo(
                key = ACCESS_TOKEN_EXPIRY_KEY,
                value = getTimeMillis() + SECONDS_TO_MILLIS_30
            )
        )
    }

    @OptIn(ExperimentalTime::class)
    fun getRefreshTokenExp(): String {
        val epochExp = getRefreshTokenExpiry()
        println("Refresh EXP $epochExp")
        return epochExp?.let {
            val date = Instant.fromEpochSeconds(it)
            formatDateInstate(date)
        } ?: NO_REFRESH_TOKEN_EXP
    }

    fun resetPersistentId() {
        viewModelScope.launch {
            saveToOpenSecureStore.save(
                AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                ""
            )
            setPersistentId()
        }
    }

    private suspend fun setPersistentId() {
        _persistentId.value = getPersistentId() ?: ""
    }

    @OptIn(ExperimentalTime::class)
    private fun formatDateInstate(date: Instant): String {
        val customFormat = DateTimeComponents.Format {
            day(); char('-'); monthNumber(); char('-'); year(); char(' ')
            hour(); char(':'); minute(); char(':'); second()
        }
        return date.format(customFormat)
    }

    companion object {
        private const val SECONDS_TO_MILLIS_30 = 30000
        private const val NO_ACCESS_TOKEN_EXP = "No access token expiry stored/ existing."
        private const val NO_REFRESH_TOKEN_EXP = "No refresh token expiry stored/ existing."
    }
}
