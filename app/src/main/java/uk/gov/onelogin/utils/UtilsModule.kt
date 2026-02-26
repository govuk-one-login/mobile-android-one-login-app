package uk.gov.onelogin.utils

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import uk.gov.onelogin.core.utils.AndroidUriParser
import uk.gov.onelogin.core.utils.Counter
import uk.gov.onelogin.core.utils.RetryCounter
import uk.gov.onelogin.core.utils.UriParser

@Module
@InstallIn(ViewModelComponent::class)
object UtilsModule {
    @Provides
    fun bindUriParser(): UriParser = AndroidUriParser()

    @Provides
    fun bindRetryCounter(): Counter = RetryCounter()
}
