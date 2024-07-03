package uk.gov.onelogin.features

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import uk.gov.android.features.FeatureFlag

private const val TOTAL_FEATURE_FLAG_IMPLEMENTATIONS = 1

class AvailableFeaturesTest {
    private val availableFeatures by lazy {
        AvailableFeatures()
    }

    @Test
    fun `All implementations of FeatureFlags are accounted for`() {
        val featureFlagList = getFeatureFlagImplementations()

        assertEquals(TOTAL_FEATURE_FLAG_IMPLEMENTATIONS, featureFlagList.size) {
            "Found the following subclasses: ${featureFlagList.names}"
        }
    }

    @Test
    fun `All enum implementations of FeatureFlags are accounted for`() {
        val featureFlagList = getFeatureFlagImplementations()
        val foundFeatureFlags = mutableSetOf<FeatureFlag>()

        featureFlagList.forEach { classInfo ->
            if (classInfo.isEnum) {
                foundFeatureFlags.addAll(
                    classInfo.enumConstantObjects as List<out FeatureFlag>
                )
            }
        }

        val distinctFoundFeatures = foundFeatureFlags - availableFeatures
        val distinctAvailableFeatures = availableFeatures - foundFeatureFlags

        assertTrue(distinctFoundFeatures.isEmpty()) {
            "There are found feature flags that aren't in the AvailableFlags class!: " +
                distinctFoundFeatures
        }
        assertTrue(distinctAvailableFeatures.isEmpty()) {
            "There are available feature flags that weren't found by the classgraph library!: " +
                distinctAvailableFeatures
        }
    }

    @Test
    fun `available features contains included feature`() {
        val featureFlagList = getFeatureFlagImplementations()

        assertTrue(availableFeatures.contains(StsFeatureFlag.STS_ENDPOINT))
    }

    @Test
    fun `adding feature flag`() {
        var availableFeatures = AvailableFeatures(mutableSetOf())
        assertFalse(availableFeatures.contains(StsFeatureFlag.STS_ENDPOINT))

        availableFeatures = availableFeatures.plus(listOf(StsFeatureFlag.STS_ENDPOINT))
        assertTrue(availableFeatures.contains(StsFeatureFlag.STS_ENDPOINT))
    }

    @Test
    fun `to String`() {
        assertEquals("AvailableFeatures[STS_ENDPOINT]", availableFeatures.toString())
    }

    @Test
    fun equals() {
        val currentAvailableFeatures = AvailableFeatures()
        val emptyAvailableFeatures = AvailableFeatures(mutableSetOf())

        assertTrue(availableFeatures.equals(currentAvailableFeatures))
        assertFalse(availableFeatures.equals(emptyAvailableFeatures))
        assertFalse(availableFeatures.equals("not an AvailableFeatures object"))
    }

    private fun getFeatureFlagImplementations(): ClassInfoList {
        var result = ClassInfoList()
        val appInfo = ClassGraph().enableAllInfo().acceptPackages(
            "uk.gov"
        )
        try {
            val scanResult = appInfo.scan()
            result = scanResult.getClassesImplementing(FeatureFlag::class.java)
        } catch (@Suppress("TooGenericExceptionCaught") exception: Exception) {
            fail(exception.message)
        }

        return result
    }
}
