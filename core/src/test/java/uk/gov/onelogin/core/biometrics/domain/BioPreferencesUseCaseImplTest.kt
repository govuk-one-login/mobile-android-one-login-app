package uk.gov.onelogin.core.biometrics.domain

import kotlin.test.Test
import kotlinx.coroutines.test.runTest
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.verifyNoInteractions
import org.mockito.kotlin.whenever

class BioPreferencesUseCaseImplTest {
    private val biometricPreferenceHandler: BiometricPreferenceHandler = mock()
    private val credentialChecker: CredentialChecker = mock()
    private val bioPreferencesUseCase: BioPreferencesUseCase = BioPreferencesUseCaseImpl(
        biometricPreferenceHandler,
        credentialChecker
    )

    @Test
    fun `test when device is secure`() = runTest {
        whenever(credentialChecker.isDeviceSecure()).thenReturn(true)

        bioPreferencesUseCase.reset()

        verifyNoInteractions(biometricPreferenceHandler)
    }

    @Test
    fun `test when device is not secure`() = runTest {
        whenever(credentialChecker.isDeviceSecure()).thenReturn(false)

        bioPreferencesUseCase.reset()

        verify(biometricPreferenceHandler).clean()
    }
}
