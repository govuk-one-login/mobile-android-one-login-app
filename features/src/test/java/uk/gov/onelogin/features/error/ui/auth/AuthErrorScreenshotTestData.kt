package uk.gov.onelogin.features.error.ui.auth

import org.mockito.kotlin.mock
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

private val navigator: Navigator = mock()
private val emptyFeatureFlag: FeatureFlags = InMemoryFeatureFlags(
    setOf()
)
private val walletFeatureFlag: FeatureFlags = InMemoryFeatureFlags(
    setOf(WalletFeatureFlag.ENABLED)
)
val authErrorViewModelWalletNotEnabled = AuthErrorViewModel(navigator, emptyFeatureFlag)
val authErrorViewModelWalletEnabled = AuthErrorViewModel(navigator, walletFeatureFlag)
