package uk.gov.onelogin.features.error.ui.appintegrity

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.kotlin.verify
import uk.gov.onelogin.core.navigation.domain.Navigator

class AppIntegrityErrorViewModelTest {
    private val navigator: Navigator = mock()
    private val sut = AppIntegrityErrorViewModel(navigator)

    @Test
    fun verifyNavigation() {
        sut.goBackToPreviousScreen()

        verify(navigator, times(2)).goBack()
    }
}
