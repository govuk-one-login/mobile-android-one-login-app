package uk.gov.output

import java.io.OutputStream

/**
 * An [OutputStream] decorator that simply defers to the other streams provided in the [streams]
 * parameter.
 *
 * ```kotlin
 * // Usage:
 * val group = OutputStreamGroup(
 *     mutableListOf<OutputStream>(
 *         anOutputStream,
 *         anotherOutputStream,
 *         // more output streams if necessary.
 *     )
 * )
 * ```
 */
data class OutputStreamGroup(
    private val streams: MutableList<OutputStream>
) : OutputStream() {

    /**
     * An [OutputStream] decorator that simply defers to the other streams provided in the [streams]
     * parameter.
     *
     * ```kotlin
     * // Usage:
     * val group = OutputStreamGroup(
     *     anOutputStream,
     *     anotherOutputStream,
     *     // more output streams if necessary.
     * )
     * ```
     */
    constructor(vararg streams: OutputStream) : this(streams.toMutableList())

    fun add(vararg streams: OutputStream) {
        this.streams.addAll(streams.toList())
    }

    override fun write(p0: Int) = streams.forEach {
        it.write(p0)
    }

    override fun write(p0: ByteArray) = streams.forEach {
        it.write(p0)
    }

    override fun write(p0: ByteArray, p1: Int, p2: Int) = streams.forEach {
        it.write(p0, p1, p2)
    }

    override fun flush() = streams.forEach(OutputStream::flush)
    override fun close() = streams.forEach(OutputStream::close)
}
