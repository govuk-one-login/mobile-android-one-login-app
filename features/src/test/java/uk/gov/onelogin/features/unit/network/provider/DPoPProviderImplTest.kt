package uk.gov.onelogin.features.unit.network.provider

import android.content.Context
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import uk.gov.android.authentication.login.refresh.SignedDPoP
import uk.gov.android.authentication.login.refresh.TestDemonstratingProofOfPossessionManager
import uk.gov.android.network.dpop.DPoPResponse
import uk.gov.android.onelogin.core.R
import uk.gov.onelogin.features.network.provider.DPoPProviderImpl

class DPoPProviderImplTest {
    private val context: Context = mock()
    private val dPoPManager = TestDemonstratingProofOfPossessionManager()
    private lateinit var provider: DPoPProviderImpl

    @BeforeEach
    fun setUp() {
        whenever(context.getString(R.string.tokenExchangeEndpoint)).thenReturn("/token")
        whenever(context.getString(R.string.stsUrl, "/token")).thenReturn(HTU)
        provider = DPoPProviderImpl(context, dPoPManager)
    }

    @Test
    fun `getRefreshDPoP returns Success when generateDPoP succeeds`() =
        runTest {
            val result = provider.getRefreshDPoP()

            assertEquals(DPoPResponse.Success(DPOP_JWT), result)
            assertEquals(HTU, dPoPManager.spyHtu)
        }

    @Test
    fun `getRefreshDPoP returns Failure when generateDPoP fails`() =
        runTest {
            val error = Exception("signing error")
            dPoPManager.response = SignedDPoP.Failure("signing error", error)

            val result = provider.getRefreshDPoP()

            assertInstanceOf(DPoPResponse.Failure::class.java, result)
            assertEquals(error, (result as DPoPResponse.Failure).error.cause)
        }

    @Test
    fun `getRefreshDPoP returns Failure with reason in message when error is null`() =
        runTest {
            dPoPManager.response = SignedDPoP.Failure("signing error", null)

            val result = provider.getRefreshDPoP()

            assertInstanceOf(DPoPResponse.Failure::class.java, result)
            assertEquals("Failed to get DPoP: signing error", (result as DPoPResponse.Failure).error.message)
        }

    companion object {
        private const val HTU = "https://token.account.gov.uk/token"
        private const val DPOP_JWT = "dpop-jwt"
    }
}
