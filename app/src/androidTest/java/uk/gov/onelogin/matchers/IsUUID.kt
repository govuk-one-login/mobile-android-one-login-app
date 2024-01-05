package uk.gov.onelogin.matchers

import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class IsUUID : BaseMatcher<Any>() {
    override fun describeTo(description: Description?) {
        description?.appendText("is a UUID")
    }

    override fun matches(item: Any?): Boolean {
        return (item is String) && item.matches(
            Regex("[0-9a-fA-F]{8}(?:-[0-9a-fA-F]{4}){3}-[0-9a-fA-F]{12}")
        )
    }
}
