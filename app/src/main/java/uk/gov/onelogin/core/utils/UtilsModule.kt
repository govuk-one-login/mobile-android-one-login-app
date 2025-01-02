package uk.gov.onelogin.core.utils

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
interface UtilsModule {
    @Binds
    fun bindUriParser(parser: AndroidUriParser): UriParser
}
