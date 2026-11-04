package uk.gov.onelogin.core.utils

import uk.gov.logging.api.analytics.logging.IS_ERROR
import uk.gov.logging.api.v3dot1.model.ViewEvent

object GAUtils {
    fun containsIsError(viewEvent: ViewEvent, expectedValue: String): Boolean {
        val map = viewEvent.asMap()
        return map[IS_ERROR] != null &&
            map[IS_ERROR] is String &&
            (map[IS_ERROR] as String) == expectedValue
    }

    const val TRUE = "true"
    const val FALSE = "false"
    const val IS_ERROR_REASON_TRUE = "Event contains is_error param and is set to true"
    const val IS_ERROR_REASON_FALSE = "Event contains is_error param and is set to false"
}
