package uk.gov.onelogin.core.utils

import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Allows for injecting time objects that are required for testing.
 *
 * Depending on the implementation, the returned type is usually [Long], however the time unit could be
 * changed based on the implementation provided.
 * [SystemTimeProvider] return epoch seconds in [Long] format, however an alternative implementation could differ.
 */
interface TimeProvider {
    /**
     * Provides a time stamp in [Long] format for 30 days from the time of calling this.
     *
     * It uses the [java.time.Instant] and is therefore linked to the device time, rather than universal.
     *
     * [SystemTimeProvider] return epoch seconds in [Long] format.
     * An alternative implementation could differ.
     */
    fun thirtyDaysFromNowTimestampInSeconds(): Long

    /**
     * Calculates a time stamp in [Long] format based on input.
     *
     * It uses the [java.time.Instant] and is therefore linked to the device time, rather than universal.
     *
     * [SystemTimeProvider.calculateExpiryTime] returns epoch seconds in [Long] format AND the input required is seconds, adding the input to a time stamp representing the current time at the point of calling the function.
     * An alternative implementation could differ.
     *
     * @param expiredInSeconds the time to be added/ subtracted from a time stamp
     */
    fun calculateExpiryTime(expiredInSeconds: Long): Long
}

class SystemTimeProvider : TimeProvider {
    override fun thirtyDaysFromNowTimestampInSeconds(): Long {
        val result =
            Instant
                .now()
                .plus(THIRTY, ChronoUnit.DAYS)
                .epochSecond
        return result
    }

    override fun calculateExpiryTime(expiredInSeconds: Long): Long = Instant.now().epochSecond + expiredInSeconds

    companion object {
        private const val THIRTY: Long = 30
    }
}
