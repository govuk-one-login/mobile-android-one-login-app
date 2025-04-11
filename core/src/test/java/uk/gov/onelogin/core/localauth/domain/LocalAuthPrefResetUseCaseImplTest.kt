package uk.gov.onelogin.core.localauth.domain

import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.android.localauth.LocalAuthManager
import uk.gov.android.localauth.preference.LocalAuthPreference

class LocalAuthPrefResetUseCaseImplTest {
    private val localAuthPreferenceRepo: LocalAuthPreferenceRepo = mock()
    private val localAuthManager: LocalAuthManager = mock()
    private val localAuthPrefResetUseCase: LocalAuthPrefResetUseCase = LocalAuthPrefResetUseCaseImpl(
        localAuthPreferenceRepo,
        localAuthManager
    )

    @Test
    fun `test when device is secure - enabled (biometrics)`() = runTest {
        whenever(localAuthManager.localAuthPreference).thenReturn(LocalAuthPreference.Enabled(true))

        localAuthPrefResetUseCase.reset()

        verifyNoInteractions(localAuthPreferenceRepo)
    }

    @Test
    fun `test when device is secure - enabled (passcode)`() = runTest {
        whenever(localAuthManager.localAuthPreference).thenReturn(LocalAuthPreference.Enabled(false))

        localAuthPrefResetUseCase.reset()

        verifyNoInteractions(localAuthPreferenceRepo)
    }

    @Test
    fun `test when device is not secure - disabled`() = runTest {
        whenever(localAuthManager.localAuthPreference).thenReturn(LocalAuthPreference.Disabled)

        localAuthPrefResetUseCase.reset()

        verify(localAuthPreferenceRepo).clean()
    }

    @Test
    fun `test when device is not secure - null`() = runTest {
        whenever(localAuthManager.localAuthPreference).thenReturn(null)

        localAuthPrefResetUseCase.reset()

        verify(localAuthPreferenceRepo).clean()
    }
}
