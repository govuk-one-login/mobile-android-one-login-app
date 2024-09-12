package uk.gov.onelogin.optin.domain.model

enum class AnalyticsOptInState {
    None, Yes, No;

    val isUnset: Boolean
        get() = this == None
}
