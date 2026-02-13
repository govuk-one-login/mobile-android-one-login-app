package uk.gov.onelogin.core.counter

class CounterImpl : Counter {
    private var count = 0

    override fun increment() {
        count++
    }

    override fun getValue(): Int = count

    override fun reset() {
        count = 0
    }
}
