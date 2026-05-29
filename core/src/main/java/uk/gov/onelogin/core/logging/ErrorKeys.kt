package uk.gov.onelogin.core.logging

import uk.gov.logging.api.v3.Logger
import uk.gov.logging.api.v3.customkey.CustomKey
import uk.gov.onelogin.core.logging.ErrorKeys.actionKey
import uk.gov.onelogin.core.logging.ErrorKeys.componentKey
import uk.gov.onelogin.core.ui.meta.ExcludeFromJacocoGeneratedReport

object ErrorKeys {
    private const val COMPONENT = "component"
    private const val ACTION = "action"

    /**
     * Creates a [CustomKey] representing the software component where an error occurred.
     * The value is automatically prefixed with "app.".
     *
     * @param component the feature or module, e.g. "login", "wallet.store", "tokens.persistent_id"
     * @sample errorKeysSample
     */
    fun componentKey(component: String): CustomKey {
        assert(component.matches(Regex("^[a-z][a-z._]*$"))) {
            "component must be snake case with dot separators only (e.g. 'login' or 'wallet.store'), got: $component"
        }
        assert(!component.startsWith("app.")) {
            "component includes the app prefix, unnecessarily: $component"
        }
        return CustomKey.StringKey(ErrorKeys.COMPONENT, "app.$component")
    }

    /**
     * Creates a [CustomKey] representing the action being attempted when an error occurred.
     *
     * @param action what was being attempted, e.g. "Redirect to app", "Get persistent ID"
     * @sample errorKeysSample
     */
    fun actionKey(action: String): CustomKey = CustomKey.StringKey(ErrorKeys.ACTION, action)
}

@ExcludeFromJacocoGeneratedReport
@Suppress("UnusedPrivateMember")
private fun errorKeysSample(logger: Logger) {
    logger.error(
        "LoginUseCase",
        "Failed to redirect to app",
        Exception("Failed to redirect to app"),
        componentKey("login"),
        actionKey("Redirect to app"),
    )
}
