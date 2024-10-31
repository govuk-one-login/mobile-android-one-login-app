package uk.gov.onelogin.integrity.model

import uk.gov.onelogin.integrity.appcheck.AppChecker
import uk.gov.onelogin.integrity.appcheck.usecase.AttestationCaller

data class AppIntegrityConfiguration(
    val attestationCaller: AttestationCaller,
    val appChecker: AppChecker
)
