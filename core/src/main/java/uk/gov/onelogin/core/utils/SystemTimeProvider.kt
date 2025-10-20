package uk.gov.onelogin.core.utils

import java.time.Instant
import java.time.temporal.ChronoUnit

fun interface TimeProvider {
    fun thirtyDaysFromNowTimestampInSeconds(): Long
}

class SystemTimeProvider : TimeProvider {
    override fun thirtyDaysFromNowTimestampInSeconds(): Long {
        val result = Instant.now()
            .plus(THIRTY, ChronoUnit.DAYS)
            .epochSecond
        println(result)
        return result
    }

    companion object {
        private const val THIRTY: Long = 30
    }
}
