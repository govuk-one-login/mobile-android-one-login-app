package uk.gov.onelogin.features.unit.home.idcheck.ui

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.core.navigation.domain.WebNavigator
import uk.gov.onelogin.features.home.idcheck.ui.HowToProveYourIdentityModalViewModel

class HowToProveYourIdentityModalViewModelTest {
    private val mockNavigator: Navigator = mock()
    private val mockWebNavigator: WebNavigator = mock()
    private val viewModel = HowToProveYourIdentityModalViewModel(
        mockNavigator,
        mockWebNavigator,
        GOV_UK_SIGN_IN_URL,
    )

    @Test
    fun `onDismissRequest calls navigator goBack`() {
        viewModel.onDismissRequest()

        verify(mockNavigator).goBack()
    }

    @Test
    fun `onGoToGovUkWebsiteClick opens gov uk sign in page in web browser`() {
        viewModel.onGoToGovUkWebsiteClick()

        verify(mockWebNavigator).openWebBrowser(GOV_UK_SIGN_IN_URL)
    }

    companion object {
        private const val GOV_UK_SIGN_IN_URL = "https://www.gov.uk/sign-in"
    }
}
