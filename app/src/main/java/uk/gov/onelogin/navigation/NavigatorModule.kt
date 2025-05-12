package uk.gov.onelogin.navigation

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import uk.gov.onelogin.core.navigation.domain.Navigator
import uk.gov.onelogin.features.navigation.domain.NavigatorImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface NavigatorModule {
    @Binds
    @Singleton
    fun bindNavigator(navigator: NavigatorImpl): Navigator
}
