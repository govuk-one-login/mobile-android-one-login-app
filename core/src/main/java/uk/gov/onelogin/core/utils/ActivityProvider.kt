package uk.gov.onelogin.core.utils

import androidx.fragment.app.FragmentActivity
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

interface ActivityProvider {
    fun getCurrentActivity(): FragmentActivity?

    fun setCurrentActivity(activity: FragmentActivity)

    fun clearActivity()
}

class ActivityProviderImpl
    @Inject
    constructor() : ActivityProvider {
        private val currentActivity = AtomicReference<FragmentActivity?>()

        override fun getCurrentActivity(): FragmentActivity? = currentActivity.get()

        override fun setCurrentActivity(activity: FragmentActivity) {
            this.currentActivity.set(activity)
        }

        override fun clearActivity() {
            this.currentActivity.set(null)
        }
    }
