package uk.gov.onelogin.features.analyticsoptin.ui

enum class OptInUIState(
    val hasButtonsOn: Boolean,
) {
    PreChoice(true),
    PostChoice(false),
}
