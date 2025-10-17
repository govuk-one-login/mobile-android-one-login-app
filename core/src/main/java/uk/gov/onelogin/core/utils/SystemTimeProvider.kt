package uk.gov.onelogin.core.utils

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

object SystemTimeProvider {
    @OptIn(ExperimentalTime::class)
    fun nowInSeconds(): Long {
        return Clock.System.now().epochSeconds
    }
}
