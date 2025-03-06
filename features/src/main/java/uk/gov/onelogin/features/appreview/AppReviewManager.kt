package uk.gov.onelogin.features.appreview

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.model.ReviewErrorCode
import jakarta.inject.Inject

fun interface AppReviewManager {
    fun requestReview(context: Context): Boolean
}

class AppReviewManagerImpl @Inject constructor(
    // Added in the constructor so it simplifies testing - might be possible to have it
    // private if we move to using Roboelectric/ PowerMockk to enable static mocking
    private val manager: ReviewManager
) : AppReviewManager {

    override fun requestReview(context: Context): Boolean {
        val request = manager.requestReviewFlow()
        var result = false
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // We got the ReviewInfo object
                val flow = manager.launchReviewFlow(context as Activity, task.result)
                flow.addOnCompleteListener {
                    Log.d(APP_REVIEW_TAG, "App review successful")
                    result = true
                }
            } else {
                // There was some problem, log or handle the error code.
                val reviewError = (task.getException() as ReviewException)

                @ReviewErrorCode val code = reviewError.errorCode
                Log.e(
                    APP_REVIEW_TAG,
                    "${reviewError.message}. Error code: $code"
                )
                result = false
            }
        }
        return result
    }

    companion object {
        const val APP_REVIEW_TAG = "AppReview"
    }
}
