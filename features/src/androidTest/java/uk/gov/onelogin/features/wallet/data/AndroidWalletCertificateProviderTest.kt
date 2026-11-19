package uk.gov.onelogin.features.wallet.data

import androidx.annotation.ArrayRes
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.hasItem
import org.hamcrest.Matchers.instanceOf
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import uk.gov.android.onelogin.features.test.R
import uk.gov.logging.api.v3.MemorisedLogger
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasException
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasLogEntry
import uk.gov.logging.api.v3.matchers.LogEntryMatchers.hasMessage

@RunWith(AndroidJUnit4::class)
class AndroidWalletCertificateProviderTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val logger = MemorisedLogger()

    @Test
    fun parsesValidCertificates() {
        val provider = createCertificateProvider(R.array.wallet_test_reader_auth_certificates_valid)

        val certificates = provider.getTrustedReaderAuthCertificates()

        assertEquals(2, certificates.size)
        assertEquals(
            "CN=Wallet Test Reader Auth CA v1",
            certificates[0].subjectX500Principal.name,
        )
        assertEquals(
            "CN=Wallet Test Reader Auth CA v2",
            certificates[1].subjectX500Principal.name,
        )

        // No errors
        assertEquals(0, logger.entries.size)
    }

    @Test
    fun logsErrorForInvalidCertificateResourceId() {
        val provider =
            createCertificateProvider(
                R.array.wallet_test_reader_auth_certificates_invalid_resource_id,
            )

        val certificates = provider.getTrustedReaderAuthCertificates()

        assertEquals(1, certificates.size)

        assertThat(
            logger,
            hasLogEntry(
                hasItem(
                    allOf(
                        hasMessage("Invalid wallet certificate resource ID"),
                        hasException(
                            instanceOf(
                                AndroidWalletCertificateProvider
                                    .InvalidCertificateException::class.java,
                            ),
                        ),
                    ),
                ),
            ),
        )
    }

    @Test
    fun logsErrorForUnexpectedCertificateFormat() {
        val provider =
            createCertificateProvider(R.array.wallet_test_reader_auth_certificates_invalid_format)

        val certificates = provider.getTrustedReaderAuthCertificates()

        assertEquals(1, certificates.size)

        assertThat(
            logger,
            hasLogEntry(
                hasItem(
                    allOf(
                        hasMessage("Failed to parse all wallet ReaderAuth certificates"),
                        hasException(
                            instanceOf(
                                AndroidWalletCertificateProvider
                                    .InvalidCertificateException::class.java,
                            ),
                        ),
                    ),
                ),
            ),
        )
    }

    private fun createCertificateProvider(@ArrayRes certificateResArray: Int) =
        AndroidWalletCertificateProvider(
            context = context,
            readerAuthCertificateArray = certificateResArray,
            logger = logger,
        )
}
