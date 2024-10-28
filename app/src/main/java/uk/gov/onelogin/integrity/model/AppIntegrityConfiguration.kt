package uk.gov.onelogin.integrity.model

import uk.gov.android.network.client.GenericHttpClient
import uk.gov.onelogin.integrity.appcheck.AppChecker

data class AppIntegrityConfiguration(
    val httpClient: GenericHttpClient,
    val attestationUrl: String,
    val appChecker: AppChecker
)
