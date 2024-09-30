package uk.gov.onelogin.core.delete.domain

import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.Assertions
import uk.gov.onelogin.signOut.domain.SignOutError
import kotlin.test.assertEquals
import kotlin.test.assertIs

@ExperimentalCoroutinesApi
class MultiCleanerTest {
    private val dispatcher = StandardTestDispatcher()
    private lateinit var cleaner: MultiCleaner

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `clean runs all sub-cleaners' clean`() = runTest {
        // Given MultiCleaner
        var actual0 = false
        var actual1 = false
        var actual2 = false
        var actual3 = false
        cleaner = MultiCleaner(
            dispatcher,
            Cleaner { actual0 = true; Result.success(Unit) },
            Cleaner { actual1 = true; Result.success(Unit) },
            Cleaner { actual2 = true; Result.success(Unit) },
            Cleaner { actual3 = true; Result.success(Unit) }
        )
        // When calling clean
        cleaner.clean()
        // Then run all sub-cleaners' clean
        assertTrue(actual0)
        assertTrue(actual1)
        assertTrue(actual2)
        assertTrue(actual3)
    }

    @Test
    fun `clean propagates sub-cleaners' exceptions`() = runTest {
        // Given MultiCleaner
        cleaner = MultiCleaner(
            dispatcher,
            Cleaner { Result.success(Unit) },
            Cleaner { throw Exception("Oh no!") },
            Cleaner { Result.success(Unit) },
            Cleaner { Result.success(Unit) }
        )
        // When calling clean
        Assertions.assertThrows(Exception::class.java) {
            runTest { cleaner.clean() }
        }
    }
}
