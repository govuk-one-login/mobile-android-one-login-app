package uk.gov.onelogin.ui.profile

import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.TestCase
import uk.gov.onelogin.tokens.usecases.GetEmail

@HiltAndroidTest
class ProfileScreenViewModelTest : TestCase() {
    private val getEmail: GetEmail = mock()

    @Test
    fun emailValid() = runTest {
        val expectedEmail = "some@mail.com"
        whenever(getEmail.invoke()).thenReturn(expectedEmail)
        val sut = ProfileScreenViewModel(getEmail)
        assertEquals(expectedEmail, sut.email)
    }

    @Test
    fun emailNull() = runTest {
        whenever(getEmail.invoke()).thenReturn(null)
        val sut = ProfileScreenViewModel(getEmail)
        assertEquals("", sut.email)
    }
}
