package uk.gov.onelogin.signOut.ui

import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import uk.gov.onelogin.signOut.domain.SignOutUseCase

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val signOutUseCase: SignOutUseCase
) : ViewModel() {
    fun signOut(context: FragmentActivity) {
        signOutUseCase(context)
    }
}