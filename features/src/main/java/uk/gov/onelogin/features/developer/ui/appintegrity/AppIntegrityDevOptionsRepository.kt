package uk.gov.onelogin.features.developer.ui.appintegrity

import uk.gov.onelogin.features.login.domain.appintegrity.AppIntegrityException

interface AppIntegrityDevOptionsRepository {
    var forceError: AppIntegrityException.AppIntegrityErrorType?
}

class AppIntegrityDevOptionsRepositoryImpl : AppIntegrityDevOptionsRepository {
    override var forceError: AppIntegrityException.AppIntegrityErrorType? = null
}
