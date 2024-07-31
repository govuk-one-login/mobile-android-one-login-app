package uk.gov.onelogin.tokens.usecases

import android.content.Context
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import uk.gov.android.securestore.AccessControlLevel
import uk.gov.android.securestore.SecureStorageConfiguration
import uk.gov.android.securestore.SecureStore
import uk.gov.onelogin.login.biooptin.BiometricPreference
import uk.gov.onelogin.login.biooptin.BiometricPreferenceHandler
import uk.gov.onelogin.tokens.Keys

class AutoInitialiseSecureStoreTest {

    private lateinit var useCase: AutoInitialiseSecureStore

    private val mockContext: Context = mock()
    private val mockSecureStore: SecureStore = mock()
    private val mockBioPrefHandler: BiometricPreferenceHandler = mock()

    @BeforeEach
    fun setUp() {
        useCase = AutoInitialiseSecureStoreImpl(mockBioPrefHandler, mockSecureStore, mockContext)
    }

    @Test
    fun `does not initialise - when pref is null`() {
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(null)

        useCase.invoke()

        verify(mockSecureStore, never()).init(any(), any())
    }

    @Test
    fun `does not initialise - when pref is NONE`() {
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.NONE)

        useCase.invoke()

        verify(mockSecureStore, never()).init(any(), any())
    }

    @Test
    fun `does initialise with PASSCODE ACL - when pref is PASSCODE`() {
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.PASSCODE)

        useCase.invoke()

        val expectedConfiguration = SecureStorageConfiguration(
            Keys.TOKEN_SECURE_STORE_ID,
            AccessControlLevel.PASSCODE
        )
        verify(mockSecureStore, times(1)).init(mockContext, expectedConfiguration)
    }

    @Test
    fun `does initialise with PASSCODE_AND_CURRENT_BIOMETRICS ACL - when pref is BIOMETRICS`() {
        whenever(mockBioPrefHandler.getBioPref()).thenReturn(BiometricPreference.BIOMETRICS)

        useCase.invoke()

        val expectedConfiguration = SecureStorageConfiguration(
            Keys.TOKEN_SECURE_STORE_ID,
            AccessControlLevel.PASSCODE_AND_CURRENT_BIOMETRICS
        )
        verify(mockSecureStore, times(1)).init(mockContext, expectedConfiguration)
    }
}
