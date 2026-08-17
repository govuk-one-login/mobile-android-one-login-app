package uk.gov.onelogin.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.navigation.domain.WebNavigator
import uk.gov.onelogin.features.navigation.domain.WebNavigatorImpl

@Module
@InstallIn(ViewModelComponent::class)
interface WebNavigatorModule {
    @Binds
    fun bindWebNavigator(navigator: WebNavigatorImpl): WebNavigator
}
