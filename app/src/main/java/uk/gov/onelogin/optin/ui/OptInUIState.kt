package uk.gov.onelogin.optin.ui

enum class OptInUIState(val hasButtonsOn: Boolean) {
    PreChoice(true),
    PostChoice(false),
}
