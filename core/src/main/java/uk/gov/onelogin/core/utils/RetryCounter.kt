package uk.gov.onelogin.core.utils

interface RetryCounter {
    fun incrementCount()
    fun getCount(): Int
    fun reset()
}

class RetryCounterImpl : RetryCounter{
    private var count: Int = 0
    override fun incrementCount() {
        count++
    }

    override fun getCount(): Int {
        return count
    }

    override fun reset() {
        count = 0
    }
}
