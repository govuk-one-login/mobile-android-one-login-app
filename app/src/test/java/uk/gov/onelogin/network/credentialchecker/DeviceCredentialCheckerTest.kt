package uk.gov.onelogin.network.credentialchecker

import android.app.KeyguardManager
import android.content.Context
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.kotlin.eq
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.onelogin.credentialchecker.DeviceCredentialChecker

class DeviceCredentialCheckerTest {
    private val mockContext: Context = mock()
    private val mockKgm: KeyguardManager = mock()

    private val checker = DeviceCredentialChecker(mockContext)

    @Test
    fun `check KeyguardManager result is returned`() {
        whenever(mockContext.getSystemService(eq(Context.KEYGUARD_SERVICE)))
            .thenReturn(mockKgm)
        whenever(mockKgm.isDeviceSecure).thenReturn(true)

        val result = checker.isDeviceSecure()

        assertTrue(result)
    }
}
