package uk.gov.onelogin.core

import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.tokens.data.TokenRepository
import uk.gov.onelogin.features.criorchestrator.CheckIdCheckSessionState
import uk.gov.onelogin.features.optin.data.AnalyticsOptInRepository
import uk.gov.onelogin.features.wallet.data.WalletRepository

@InstallIn(SingletonComponent::class)
@EntryPoint
interface ApplicationEntryPoint {
    fun isIdCheckSessionActive(): CheckIdCheckSessionState
    fun localAuthManager(): LocalAuthManager
    fun tokenRepository(): TokenRepository
    fun navigator(): Navigator
    fun walletDeeplinkRepo(): WalletRepository
    fun analyticsOptInRepo(): AnalyticsOptInRepository
}
