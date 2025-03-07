package uk.gov.onelogin.features.signout.domain

import androidx.fragment.app.FragmentActivity

fun interface SignOutUseCase {
    suspend fun invoke(activityFragment: FragmentActivity)
}
