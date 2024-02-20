package uk.gov.onelogin.matchers

import android.content.Intent
import android.net.Uri
import org.hamcrest.BaseMatcher
import org.hamcrest.Description
import org.hamcrest.TypeSafeMatcher

class MatchesUri(
    private val host: String?,
    private val path: String?,
    private val parameters: Map<String, BaseMatcher<Any>> = mapOf()
) : TypeSafeMatcher<Intent>() {

    override fun describeTo(description: Description?) {
        description?.appendText("matches uri")
    }

    @Suppress("ReturnCount")
    override fun matchesSafely(item: Intent?): Boolean {
        return item?.let {
            it.data?.let {
                val data: Uri = item.data!!

                if (data.host != host) {
                    return false
                }

                if (data.path != path) {
                    return false
                }

                parameters.forEach { parameter ->
                    val matcher = parameter.value
                    val entry = data.getQueryParameter(parameter.key)

                    return matcher.matches(entry)
                }

                true
            } ?: false
        } ?: false
    }
}
