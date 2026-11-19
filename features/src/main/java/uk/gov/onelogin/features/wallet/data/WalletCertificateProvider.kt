package uk.gov.onelogin.features.wallet.data

import android.content.Context
import androidx.annotation.ArrayRes
import androidx.core.content.res.use
import java.security.cert.CertificateFactory
import java.security.cert.X509Certificate
import uk.gov.android.onelogin.features.R
import uk.gov.logging.api.LogTagProvider
import uk.gov.logging.api.v3.Logger

fun interface WalletCertificateProvider {
    fun getTrustedReaderAuthCertificates(): List<X509Certificate>
}

class AndroidWalletCertificateProvider(
    private val context: Context,
    @ArrayRes
    private val readerAuthCertificateArray: Int = R.array.wallet_reader_auth_certificates,
    private val logger: Logger,
) : WalletCertificateProvider,
    LogTagProvider {
    override fun getTrustedReaderAuthCertificates(): List<X509Certificate> {
        val certIdArray = context.resources.obtainTypedArray(readerAuthCertificateArray)
        val certIds = certIdArray.use {
            (0 until it.length()).mapNotNull { index ->
                val rawResourceId = it.getResourceId(index, 0)
                if (rawResourceId == 0) {
                    logError("Invalid wallet certificate resource ID")
                    return@mapNotNull null
                }
                rawResourceId
            }
        }

        val factory = CertificateFactory.getInstance(CERTIFICATE_TYPE)
        val certificates = certIds.flatMap { id ->
            context.resources.openRawResource(id).use { inputStream ->
                factory.generateCertificates(inputStream)
                    .filterIsInstance<X509Certificate>()
            }
        }

        if (certificates.size != certIds.size) {
            logError("Failed to parse all wallet ReaderAuth certificates")
        }

        return certificates
    }

    private fun logError(message: String) =
        logger.error(message, InvalidCertificateException(message))

    class InvalidCertificateException(message: String) : IllegalArgumentException(message)

    private companion object {
        const val CERTIFICATE_TYPE = "X.509"
    }
}
