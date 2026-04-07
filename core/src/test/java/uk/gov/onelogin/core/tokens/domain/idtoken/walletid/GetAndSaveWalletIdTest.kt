package uk.gov.onelogin.core.tokens.domain.idtoken.walletid

import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock
import uk.gov.logging.testdouble.SystemLogger
import uk.gov.onelogin.core.tokens.domain.idtoken.walletId.ExtractAndSaveWalletIdImpl
import uk.gov.onelogin.core.tokens.domain.save.SaveToOpenSecureStore
import uk.gov.onelogin.core.tokens.utils.JwtExtractor
import uk.gov.onelogin.core.tokens.utils.JwtExtractorImpl
import kotlin.test.assertEquals

class GetAndSaveWalletIdTest {
    private val expectedWalletId = "test_wallet_id"
    private val idTokenWithWalletId =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".ewoiZW1haWwiOiAibW9ja0BlbWFpbC5jb20iLAoidWsuZ292LmFjY291bnQudG9rZW4v" +
            "d2FsbGV0U3RvcmVJZCI6ICJ0ZXN0X3dhbGxldF9pZCIKfQ" +
            // payload contains "email": "email@mail.com"
            // payload contains "uk.gov.account.token/walletStoreId": "test_wallet_id"
            ".mHuqqrjGNsVpzm-8jiZ8VnlWuAVSlexyjDsOX7YDB6Q"
    private val idTokenWithoutWalletId =
        "eyJhbGciOiJIUzI1NiJ9" +
            ".e30." + // no walletId in the payload
            "ZRrHA1JJJW8opsbCGfG_HACGpVUMN_a9IV7pAx_Zmeo"
    private val extractFromJson: JwtExtractor = JwtExtractorImpl()
    private val saveToOpenSecureStore: SaveToOpenSecureStore = mock()
    private val logger = SystemLogger()

    val sut = ExtractAndSaveWalletIdImpl(extractFromJson, saveToOpenSecureStore, logger)

    @Test
    fun `success scenario`() =
        runTest {
            val emailResponse = sut.extractAndSave(idTokenWithWalletId)
            assertEquals(expectedWalletId, emailResponse)
        }

    @Test
    fun `missing wallet id scenario`() =
        runTest {
            val emailResponse = sut.extractAndSave(idTokenWithoutWalletId)
            assertEquals(null, emailResponse)
        }

    @Test
    fun `malformed Id token scenario`() =
        runTest {
            val emailResponse = sut.extractAndSave("not an id token")
            assertEquals(null, emailResponse)
            assertEquals(1, logger.size)
        }
}
