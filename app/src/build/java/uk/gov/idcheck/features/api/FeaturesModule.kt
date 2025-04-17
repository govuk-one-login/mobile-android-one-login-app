package uk.gov.idcheck.features.api

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import uk.gov.idcheck.readid.common.api.brp.features.BRCFeatures
import uk.gov.idcheck.readid.common.api.passport.photo.events.PassportPhotoScanFeatures
import uk.gov.idcheck.ui.presentation.buttons.support.SupportButtonFeatureFlag
import uk.gov.idcheck.sdk.features.PhotoQualityCheckFeatureFlag
import uk.gov.idcheck.sdk.features.PostMvpFeatureFlag
import uk.gov.idcheck.sdk.features.ReadIDFeatureFlag
import uk.gov.idcheck.sdk.features.RestartJourneyErrorFeatureFlag
import uk.gov.idcheck.sdk.features.SubmitResultFeatureFlag
import uk.gov.idcheck.sdk.features.VeriffFeatureFlag

@Module
@InstallIn(
    SingletonComponent::class
)
object FeaturesModule {

    @Provides
    @Singleton
    fun providesFeatureFlags(): InMemoryFeatureFlags = InMemoryFeatureFlags(
        BRCFeatures.BRC,
        BRCFeatures.READID_NFC_HELP_CAROUSEL,
        BRCFeatures.READID_NFC_INSTRUCTIONS,
        PassportPhotoScanFeatures.ENHANCED_PASSPORT_PHOTO_LOGGING,
        PhotoQualityCheckFeatureFlag.PHOTO_QUALITY,
        PostMvpFeatureFlag.POST_MVP,
        ReadIDFeatureFlag.READ_ID_SCANNER,
        RestartJourneyErrorFeatureFlag.RESTART_JOURNEY,
        SubmitResultFeatureFlag.SUBMIT_RESULT,
        SupportButtonFeatureFlag.SUPPORT_BUTTON,
        VeriffFeatureFlag.VERIFF
    )
}
