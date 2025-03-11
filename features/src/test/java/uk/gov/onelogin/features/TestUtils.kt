package uk.gov.onelogin.features

import uk.gov.logging.api.v3dot1.model.RequiredParameters
import uk.gov.logging.api.v3dot1.model.TrackEvent
import uk.gov.logging.api.v3dot1.model.ViewEvent
import uk.gov.onelogin.features.appinfo.data.model.AppInfoData

object TestUtils {
    val appInfoData =
        AppInfoData(
            apps =
            AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "1.0.0",
                    releaseFlags =
                    AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(true)
                )
            )
        )

    val appInfoDataAppUnavailable =
        AppInfoData(
            apps =
            AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "1.0.0",
                    releaseFlags =
                    AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = false,
                    featureFlags = AppInfoData.FeatureFlags(true)
                )
            )
        )

    val additionalAppInfoData =
        AppInfoData(
            apps =
            AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "1.0.0",
                    releaseFlags =
                    AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(true)
                )
            )
        )

    val appInfoDataDisabledFeatures =
        AppInfoData(
            apps =
            AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "1.0.0",
                    releaseFlags =
                    AppInfoData.ReleaseFlags(
                        false,
                        false,
                        false
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(false)
                )
            )
        )

    val updateRequiredAppInfoData =
        AppInfoData(
            apps =
            AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "2.0.0",
                    releaseFlags =
                    AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(true)
                )
            )
        )

    val extractVersionErrorAppInfoData =
        AppInfoData(
            apps =
            AppInfoData.App(
                AppInfoData.AppInfo(
                    minimumVersion = "One.Two.Zero",
                    releaseFlags =
                    AppInfoData.ReleaseFlags(
                        true,
                        true,
                        true
                    ),
                    available = true,
                    featureFlags = AppInfoData.FeatureFlags(true)
                )
            )
        )

    internal sealed class TrackEventTestCase(val runTrackFunction: () -> Unit) {
        data class Icon(val trackFunction: () -> Unit, val text: String) :
            TrackEventTestCase(trackFunction)

        data class Link(val trackFunction: () -> Unit, val domain: String, val text: String) :
            TrackEventTestCase(trackFunction)

        data class Button(val trackFunction: () -> Unit, val text: String) :
            TrackEventTestCase(trackFunction)

        data class Screen(val trackFunction: () -> Unit, val name: String, val id: String) :
            TrackEventTestCase(trackFunction)
    }

    internal fun executeTrackEventTestCase(
        testCases: TrackEventTestCase,
        requiredParameters: RequiredParameters
    ) = when (testCases) {
        is TrackEventTestCase.Link ->
            TrackEvent.Link(
                isExternal = false,
                domain = testCases.domain,
                text = testCases.text,
                params = requiredParameters
            )

        is TrackEventTestCase.Button ->
            TrackEvent.Button(
                text = testCases.text,
                params = requiredParameters
            )

        is TrackEventTestCase.Icon ->
            TrackEvent.Icon(
                text = testCases.text,
                params = requiredParameters
            )

        is TrackEventTestCase.Screen ->
            ViewEvent.Screen(
                name = testCases.name,
                id = testCases.id,
                params = requiredParameters
            )
    }.also { testCases.runTrackFunction() }
}
