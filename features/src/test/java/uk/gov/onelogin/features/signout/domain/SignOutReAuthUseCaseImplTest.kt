package uk.gov.onelogin.features.signout.domain

import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever
import uk.gov.onelogin.core.biometrics.domain.BiometricPreferenceHandler
import uk.gov.onelogin.core.biometrics.domain.CredentialChecker

class SignOutReAuthUseCaseImplTest {
    private val biometricPreferenceHandler: BiometricPreferenceHandler = mock()
    private val credentialChecker: CredentialChecker = mock()
    private val signOutReAuthUseCase: SignOutReAuthUseCase = SignOutReAuthUseCaseImpl(
        biometricPreferenceHandler,
        credentialChecker
    )

    @Test
    fun `test when device is secure`() = runTest {
        whenever(credentialChecker.isDeviceSecure()).thenReturn(true)

        signOutReAuthUseCase.resetBioPreferences()

        verifyNoInteractions(biometricPreferenceHandler)
    }

    @Test
    fun `test when device is not secure`() = runTest {
        whenever(credentialChecker.isDeviceSecure()).thenReturn(false)

        signOutReAuthUseCase.resetBioPreferences()

        verify(biometricPreferenceHandler).clean()
    }
}
