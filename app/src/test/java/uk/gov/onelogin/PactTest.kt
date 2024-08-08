package uk.gov.onelogin

import au.com.dius.pact.consumer.junit5.PactTestFor
import au.com.dius.pact.core.model.PactSpecVersion
import org.junit.jupiter.api.Test

@PactTestFor(providerName = "ArticlesProvider", pactVersion = PactSpecVersion.V3)
class PactTest {

    @Test
    fun `something happens`() {

    }

}