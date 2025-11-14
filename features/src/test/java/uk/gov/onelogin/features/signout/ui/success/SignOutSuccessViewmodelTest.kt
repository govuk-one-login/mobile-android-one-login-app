package uk.gov.onelogin.features.signout.ui.success

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.android.featureflags.FeatureFlags
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator

class SignOutSuccessViewmodelTest {
    private lateinit var navigator: Navigator
    private lateinit var featureFlags: FeatureFlags
    private lateinit var viewModel: SignOutSuccessViewModel

    @BeforeEach
    fun setup() {
        navigator = mock()
        featureFlags = mock()
        viewModel = SignOutSuccessViewModel(navigator)
    }

    @Test
    fun testNavigation() {
        viewModel.navigateStart()

        verify(navigator).navigate(LoginRoutes.Root, true)
    }
}
