package uk.gov.onelogin.signOut.ui

import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.signOut.domain.SignOutUseCase

class SignOutViewModelTest {

    private val signOutUseCase: SignOutUseCase = mock()
    private val sut = SignOutViewModel(signOutUseCase)

    @Test
    fun signOut() {
        sut.signOut()
        verify(signOutUseCase).invoke()
    }
}
