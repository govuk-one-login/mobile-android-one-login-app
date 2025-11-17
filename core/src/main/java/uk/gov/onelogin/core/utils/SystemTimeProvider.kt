package uk.gov.onelogin.core.utils

import java.time.Instant
import java.time.temporal.ChronoUnit

interface TimeProvider {
    fun thirtyDaysFromNowTimestampInSeconds(): Long

    fun calculateExpiryTime(expiredInSeconds: Long): Long
}

class SystemTimeProvider : TimeProvider {
    override fun thirtyDaysFromNowTimestampInSeconds(): Long {
        val result = Instant.now()
            .plus(THIRTY, ChronoUnit.DAYS)
            .epochSecond
        return result
    }

    override fun calculateExpiryTime(expiredInSeconds: Long): Long {
        return Instant.now().epochSecond + expiredInSeconds
    }

    companion object {
        private const val THIRTY: Long = 30
    }
}
