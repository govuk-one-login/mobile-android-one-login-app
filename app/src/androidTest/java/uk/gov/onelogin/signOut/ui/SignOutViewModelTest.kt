package uk.gov.onelogin.signOut.ui

import androidx.fragment.app.FragmentActivity
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import uk.gov.onelogin.signOut.domain.SignOutUseCase

class SignOutViewModelTest {

    private val signOutUseCase: SignOutUseCase = mock()
    private val activityFragment: FragmentActivity = mock()
    private val sut = SignOutViewModel(signOutUseCase)

    @Test
    fun signOut() = runBlocking {
        sut.signOut(activityFragment)
        verify(signOutUseCase).invoke(any())
    }
}
