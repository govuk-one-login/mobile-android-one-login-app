package uk.gov.onelogin.login.state

class StateGeneratorStub(
    private val state: String
): IStateGenerator {
    override fun generate(): String = state
}
