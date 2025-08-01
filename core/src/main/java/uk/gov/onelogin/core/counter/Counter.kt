package uk.gov.onelogin.core.counter

interface Counter {
    fun increment()
    fun getValue(): Int
    fun reset()
}
