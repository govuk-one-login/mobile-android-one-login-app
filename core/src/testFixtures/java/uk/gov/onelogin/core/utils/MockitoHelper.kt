package uk.gov.onelogin.core.utils

import org.mockito.Mockito

/**
 * Helper class to allow matcher function any() to
 * work with latest version of Kotlin
 */
object MockitoHelper {
    fun <T> anyObject(): T {
        Mockito.any<T>()
        return uninitialized()
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> uninitialized(): T = null as T
}
