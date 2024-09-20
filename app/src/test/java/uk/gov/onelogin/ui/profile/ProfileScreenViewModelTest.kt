package uk.gov.onelogin.ui.profile

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.onelogin.navigation.Navigator
import uk.gov.onelogin.signOut.SignOutRoutes
import uk.gov.onelogin.tokens.usecases.GetEmail
import uk.gov.onelogin.ui.LocaleUtils

class ProfileScreenViewModelTest {
    private lateinit var viewModel: ProfileScreenViewModel

    private val mockNavigator: Navigator = mock()
    private val mockLocaleUtils: LocaleUtils = mock()
    private val mockGetEmail: GetEmail = mock()

    @BeforeEach
    fun setup() {
        viewModel = ProfileScreenViewModel(
            mockNavigator,
            mockLocaleUtils,
            mockGetEmail
        )
    }

    @Test
    fun `email is empty when getEmail returns null`() {
        whenever(mockGetEmail.invoke()).thenReturn(null)
        setup()

        assert(viewModel.email.isEmpty())
    }

    @Test
    fun `email is given when getEmail returns a value`() {
        whenever(mockGetEmail.invoke()).thenReturn("test")
        setup()

        assertEquals("test", viewModel.email)
    }

    @Test
    fun `locale is result of getLocale()`() {
        whenever(mockLocaleUtils.getLocale()).thenReturn("test")
        setup()

        assertEquals("test", viewModel.locale)
    }

    @Test
    fun `goToSignOut() correctly navigates to sign out`() {
        viewModel.goToSignOut()

        verify(mockNavigator).navigate(SignOutRoutes.Start, false)
    }
}
