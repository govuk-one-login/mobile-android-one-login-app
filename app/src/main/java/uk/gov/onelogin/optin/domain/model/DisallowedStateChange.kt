package uk.gov.onelogin.optin.domain.model

class DisallowedStateChange(
    val state: AnalyticsOptInState = AnalyticsOptInState.None
): Exception("The following state ($state) cannot be changed to")
