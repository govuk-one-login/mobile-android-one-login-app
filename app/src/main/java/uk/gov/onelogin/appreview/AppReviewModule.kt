package uk.gov.onelogin.appreview

import android.content.Context
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.features.appreview.AppReviewManager
import uk.gov.onelogin.features.appreview.AppReviewManagerImpl

@Module
@InstallIn(SingletonComponent::class)
class AppReviewModule {
    @Provides
    fun provideAppReviewManager(
        @ApplicationContext
        context: Context
    ): AppReviewManager {
        val reviewManager: ReviewManager = ReviewManagerFactory.create(context)
        return AppReviewManagerImpl(reviewManager)
    }
}
