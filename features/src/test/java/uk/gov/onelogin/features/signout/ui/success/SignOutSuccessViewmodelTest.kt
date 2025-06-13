package uk.gov.onelogin.features.signout.ui.success

import junit.framework.TestCase.assertFalse
import kotlin.test.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.featureflags.data.WalletFeatureFlag

class SignOutSuccessViewmodelTest {
    private lateinit var navigator: Navigator
    private lateinit var featureFlags: FeatureFlags
    private lateinit var viewModel: SignOutSuccessViewModel

    @BeforeEach
    fun setup() {
        navigator = mock()
        featureFlags = mock()
        viewModel = SignOutSuccessViewModel(navigator, featureFlags)
    }

    @Test
    fun testNavigation() {
        viewModel.navigateStart()

        verify(navigator).navigate(LoginRoutes.Root, true)
    }

    @Test
    fun testWalletEnabled() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(true)
        assertTrue(viewModel.isWalletEnabled())
    }

    @Test
    fun testWalletDisabled() {
        whenever(featureFlags[WalletFeatureFlag.ENABLED]).thenReturn(false)
        assertFalse(viewModel.isWalletEnabled())
    }
}
