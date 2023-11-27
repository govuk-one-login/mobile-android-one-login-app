package uk.gov.onelogin.login.state

import java.util.UUID
import javax.inject.Inject

class StateGenerator @Inject constructor(): IStateGenerator {
    override fun generate(): String = UUID.randomUUID().toString()
}
