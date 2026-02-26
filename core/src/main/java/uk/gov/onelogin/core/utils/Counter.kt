package uk.gov.onelogin.core.utils

interface Counter {
    fun incrementCount()

    fun getCount(): Int

    fun reset()
}

class RetryCounter : Counter {
    private var count: Int = 0

    override fun incrementCount() {
        count++
    }

    override fun getCount(): Int = count

    override fun reset() {
        count = 0
    }
}
