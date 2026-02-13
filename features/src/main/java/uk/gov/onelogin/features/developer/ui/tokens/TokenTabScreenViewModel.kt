package uk.gov.onelogin.features.developer.ui.tokens

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
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
import uk.gov.onelogin.core.tokens.utils.AuthTokenStoreKeys.REFRESH_TOKEN_EXPIRY_KEY
import uk.gov.onelogin.core.utils.AccessToken
import uk.gov.onelogin.core.utils.RefreshToken
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@Suppress("TooManyFunctions")
@HiltViewModel
class TokenTabScreenViewModel
    @Inject
    constructor(
        private val saveTokenExpiry: SaveTokenExpiry,
        private val saveToOpenSecureStore: SaveToOpenSecureStore,
        private val getPersistentId: GetPersistentId,
        @param:AccessToken
        private val getAccessTokenExpiry: GetTokenExpiry,
        @param:RefreshToken
        private val getRefreshTokenExpiry: GetTokenExpiry,
    ) : ViewModel() {
        private val _persistentId = MutableStateFlow("")
        val persistentId: StateFlow<String>
            get() = _persistentId.asStateFlow()
        private val _accessTokenExpiry = MutableStateFlow("")
        val accessTokenExpiry: StateFlow<String>
            get() = _accessTokenExpiry.asStateFlow()
        private val _refreshTokenExpiry = MutableStateFlow("")
        val refreshTokenExpiry: StateFlow<String>
            get() = _refreshTokenExpiry.asStateFlow()

        init {
            viewModelScope.launch {
                setPersistentId()
                getAccessTokenExp()
                getRefreshTokenExp()
            }
        }

        @OptIn(ExperimentalTime::class)
        suspend fun getAccessTokenExp() {
            val epochExp = getAccessTokenExpiry()
            _accessTokenExpiry.value = epochExp?.let {
                val date = Instant.fromEpochMilliseconds(it)
                formatDateInstate(date)
            } ?: NO_ACCESS_TOKEN_EXP
        }

        fun resetAccessTokenExp() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = ACCESS_TOKEN_EXPIRY_KEY,
                        value =
                            java.time.Instant
                                .now()
                                .minus(MINUTES_1, ChronoUnit.MINUTES)
                                .toEpochMilli(),
                    ),
                )
            }
        }

        fun resetRefreshTokenExp() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = REFRESH_TOKEN_EXPIRY_KEY,
                        value =
                            java.time.Instant
                                .now()
                                .minus(MINUTES_1, ChronoUnit.MINUTES)
                                .epochSecond,
                    ),
                )
            }
        }

        fun setAccessTokenExpireTo30Seconds() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = ACCESS_TOKEN_EXPIRY_KEY,
                        value =
                            java.time.Instant
                                .now()
                                .plus(SECONDS_30, ChronoUnit.SECONDS)
                                .toEpochMilli(),
                    ),
                )
            }
        }

        fun setRefreshTokenExpireTo30Seconds() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = REFRESH_TOKEN_EXPIRY_KEY,
                        value =
                            java.time.Instant
                                .now()
                                .plus(SECONDS_30, ChronoUnit.SECONDS)
                                .epochSecond,
                    ),
                )
            }
        }

        fun updateRefreshTokenToNull() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(REFRESH_TOKEN_EXPIRY_KEY, 0),
                )
            }
        }

        fun updateAccessTokenToNull() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(ACCESS_TOKEN_EXPIRY_KEY, 0),
                )
            }
        }

        @OptIn(ExperimentalTime::class)
        fun setRefreshTokenExpireTo5Minutes() {
            viewModelScope.launch {
                saveTokenExpiry.saveExp(
                    ExpiryInfo(
                        key = REFRESH_TOKEN_EXPIRY_KEY,
                        value =
                            java.time.Instant
                                .now()
                                .plus(MINUTES_5, ChronoUnit.MINUTES)
                                .epochSecond,
                    ),
                )
            }
        }

        @OptIn(ExperimentalTime::class)
        suspend fun getRefreshTokenExp() {
            val epochExp = getRefreshTokenExpiry()
            _refreshTokenExpiry.value = epochExp?.let {
                val date = Instant.fromEpochSeconds(it)
                formatDateInstate(date)
            } ?: NO_REFRESH_TOKEN_EXP
        }

        fun resetPersistentId() {
            viewModelScope.launch {
                saveToOpenSecureStore.save(
                    AuthTokenStoreKeys.PERSISTENT_ID_KEY,
                    "",
                )
                setPersistentId()
            }
        }

        private suspend fun setPersistentId() {
            _persistentId.value = getPersistentId() ?: ""
        }

        @OptIn(ExperimentalTime::class)
        private fun formatDateInstate(date: Instant): String {
            val customFormat =
                DateTimeComponents.Format {
                    day()
                    char('-')
                    monthNumber()
                    char('-')
                    year()
                    char(' ')
                    hour()
                    char(':')
                    minute()
                    char(':')
                    second()
                }
            return date.format(customFormat)
        }

        companion object {
            private const val MINUTES_1: Long = 1
            private const val MINUTES_5: Long = 5
            private const val SECONDS_30: Long = 30
            private const val NO_ACCESS_TOKEN_EXP = "No access token expiry stored/ existing."
            private const val NO_REFRESH_TOKEN_EXP = "No refresh token expiry stored/ existing."
        }
    }
