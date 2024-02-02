package uk.gov.android.features

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class InMemoryFeatureFlagsTest {

    @Test
    fun `flags may have features added in an immutable way`() {
        val updateByFlag =
            FeatureFlagsTestData.originalFeatures + FeatureFlagsTestData.unitTestFeature
        val updateBySet =
            FeatureFlagsTestData.originalFeatures + FeatureFlagsTestData.unitTestFeature

        assertAgainstOriginalFeature(updateByFlag, updateBySet)
    }

    @Test
    fun `flags may have features removed in an immutable way`() {
        val updateByFlag = FeatureFlagsTestData.originalFeatures -
            FeatureFlagsTestData.existingFeature
        val updateBySet =
            FeatureFlagsTestData.originalFeatures - FeatureFlagsTestData.existingFeature

        assertAgainstOriginalFeature(updateByFlag, updateBySet)
    }

    @Test
    fun `enabled features defined by existence within private Set`() {
        assertTrue(
            "The API feature should be within the FeatureFlags object!",
            FeatureFlagsTestData.originalFeatures[FeatureFlagsTestData.existingFeature]
        )
        assertFalse(
            "The anonymous object should not have it's feature enabled!",
            FeatureFlagsTestData.originalFeatures[FeatureFlagsTestData.unitTestFeature]
        )
        assertFalse(
            "The and logic should have been false due to the disabled unitTestFeature!",
            FeatureFlagsTestData.originalFeatures[
                FeatureFlagsTestData.existingFeature,
                FeatureFlagsTestData.unitTestFeature
            ]
        )
    }

    private fun assertAgainstOriginalFeature(
        updateByFlag: InMemoryFeatureFlags,
        updateBySet: InMemoryFeatureFlags
    ) {
        assertNotEquals(
            "originalFeatures should have created a different object via Flag!",
            FeatureFlagsTestData.originalFeatures,
            updateByFlag
        )
        assertNotEquals(
            "originalFeatures should have created a different object via Set!",
            FeatureFlagsTestData.originalFeatures,
            updateBySet
        )
        assertEquals(
            "updateByFlag should value-match updateBySet!",
            updateByFlag,
            updateBySet
        )
    }
}
