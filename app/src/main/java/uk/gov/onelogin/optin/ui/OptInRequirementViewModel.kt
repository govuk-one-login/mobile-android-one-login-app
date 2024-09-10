package uk.gov.onelogin.optin.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.optin.domain.repository.OptInRepository
import javax.inject.Inject

@HiltViewModel
class OptInRequirementViewModel @Inject constructor(
    repository: OptInRepository
): ViewModel() {
    val isOptInRequired = repository.isOptInPreferenceRequired()
}