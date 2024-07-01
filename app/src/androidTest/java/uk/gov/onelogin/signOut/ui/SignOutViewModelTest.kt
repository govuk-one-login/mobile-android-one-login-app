package uk.gov.onelogin.signOut.ui

import androidx.fragment.app.FragmentActivity
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify
import uk.gov.onelogin.signOut.domain.SignOutUseCase

class SignOutViewModelTest {

    private val fragmentActivity: FragmentActivity = mock()
    private val signOutUseCase: SignOutUseCase = mock()
    private val sut = SignOutViewModel(signOutUseCase)

    @Test
    fun signOut() {
        sut.signOut(fragmentActivity)
        verify(signOutUseCase).invoke(fragmentActivity)
    }
}
