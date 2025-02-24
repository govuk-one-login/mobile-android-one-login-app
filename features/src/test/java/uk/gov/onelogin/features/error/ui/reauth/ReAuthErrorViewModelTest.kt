package uk.gov.onelogin.features.error.ui.reauth

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator

class ReAuthErrorViewModelTest {
    private val mockNavigator: Navigator = mock()
    private val sut = ReAuthErrorViewModel(mockNavigator)

    @Test
    fun `navigate to sign in`() =
        runTest {
            sut.navigateToSignIn()

            verify(mockNavigator).navigate(LoginRoutes.Start, popUpToInclusive = true)
        }
}
