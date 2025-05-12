package uk.gov.onelogin.features.error.ui.auth

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.android.featureflags.InMemoryFeatureFlags
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

class AuthErrorViewModelTest {
    private lateinit var featureFlags: FeatureFlags
    private val mockNavigator: Navigator = mock()

    @Test
    fun `navigate to sign in`() =
        runTest {
            featureFlags =
                InMemoryFeatureFlags(
                    setOf()
                )
            val sut = AuthErrorViewModel(mockNavigator, featureFlags)

            sut.navigateToSignIn()

            verify(mockNavigator).navigate(LoginRoutes.Start, popUpToInclusive = true)
        }

    @Test
    fun `wallet enabled`() =
        runTest {
            featureFlags =
                InMemoryFeatureFlags(
                    setOf(WalletFeatureFlag.ENABLED)
                )
            val sut = AuthErrorViewModel(mockNavigator, featureFlags)

            val result = sut.walletEnabled

            assertTrue(result)
        }

    @Test
    fun `wallet disabled`() =
        runTest {
            featureFlags =
                InMemoryFeatureFlags(
                    setOf()
                )
            val sut = AuthErrorViewModel(mockNavigator, featureFlags)

            val result = sut.walletEnabled

            assertFalse(result)
        }
}
