package uk.gov.onelogin.features.analyticsoptin.data

enum class AnalyticsOptInState {
    None,
    Yes,
    No,
    ;

    val isUnset: Boolean
        get() = this == None
}
