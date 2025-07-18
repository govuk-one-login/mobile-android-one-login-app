package uk.gov.onelogin.features.error.ui.signin

import android.app.Activity
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class SignInErrorUnrecoverableViewModelTest {
    private val sut = SignInErrorUnrecoverableViewModel()
    private val mockActivity: Activity = mock()

    @Test
    fun testExit() {
        sut.exitApp(mockActivity)

        verify(mockActivity).finishAndRemoveTask()
    }
}
