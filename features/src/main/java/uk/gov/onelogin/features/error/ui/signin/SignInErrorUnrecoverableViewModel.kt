package uk.gov.onelogin.features.error.ui.signin

import android.app.Activity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SignInErrorUnrecoverableViewModel
    @Inject
    constructor() : ViewModel() {
        fun exitApp(activity: Activity) {
            activity.finishAndRemoveTask()
        }
    }
