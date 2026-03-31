package uk.gov.onelogin.features.analyticsoptin.ui

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import uk.gov.onelogin.features.analyticsoptin.data.OptInRepository
import javax.inject.Inject

@HiltViewModel
class OptInRequirementViewModel
    @Inject
    constructor(
        repository: OptInRepository,
    ) : ViewModel() {
        val isOptInRequired = repository.isOptInPreferenceRequired()
    }
