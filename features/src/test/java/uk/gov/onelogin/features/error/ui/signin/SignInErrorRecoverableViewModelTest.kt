package uk.gov.onelogin.features.error.ui.signin

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.core.navigation.data.LoginRoutes
import uk.gov.onelogin.core.navigation.domain.Navigator

class SignInErrorRecoverableViewModelTest {
    private val mockNavigator: Navigator = mock()
    private val sut = SignInErrorRecoverableViewModel(mockNavigator)

    @Test
    fun testOnBack() {
        sut.onBack()
        verify(mockNavigator).navigate(LoginRoutes.Welcome, true)
    }

    @Test
    fun testOnClick() {
        sut.onClick()
        verify(mockNavigator).navigate(LoginRoutes.Start, true)
    }
}
