package uk.gov.onelogin.features.error.ui.signout

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.verify
import uk.gov.onelogin.core.navigation.domain.Navigator

class SignOutWalletErrorViewModelTest {
    private val navigator: Navigator = mock()
    private val sut = SignOutErrorViewModel(navigator)

    @Test
    fun verifyNavigation() {
        sut.goBackToSettingsScreen()

        verify(navigator, times(2)).goBack()
    }
}
