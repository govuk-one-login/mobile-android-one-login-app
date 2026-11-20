package uk.gov.onelogin

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

/**
 * Validates every network_security_config.xml
 *
 * Android matches <domain> against the bare hostname only, so values such as
 * "api.example.com/", "https://api.example.com" or "api.example.com:443"
 * never match
 */
class NetworkSecurityConfigTest {

    // Local unit tests run with the module directory (e.g. app/) as the working directory.
    private val sourceRoot = File("src")

    private val configFiles: List<File> =
        sourceRoot.walkTopDown()
            .filter {
                it.isFile && it.name == "network_security_config.xml"
            }
            .toList()

    private val label = "[a-z0-9]([a-z0-9-]{0,61}[a-z0-9])?"
    private val hostname = Regex(
        """^(?=.{1,253}$)$label(\.$label)*$""",
        RegexOption.IGNORE_CASE,
    )

    @Test
    fun `network security config files are found`() {
        assertTrue(
            configFiles.isNotEmpty(),
            "No network_security_config.xml found under ${sourceRoot.absolutePath}",
        )
    }

    @Test
    fun `problems are detected in invalid config file`() {
        val problems = findProblems(File("src/test/res/xml/invalid_network_security_config.xml"))
        assertEquals(
            """
                src/test/res/xml/invalid_network_security_config.xml: "invalid-double-dot..test-example.gov.uk" is not a bare hostname
                src/test/res/xml/invalid_network_security_config.xml: "invalid-trailing-slash.test-example.gov.uk/" has a trailing '/'
                src/test/res/xml/invalid_network_security_config.xml: "https://invalid-scheme.test-example.gov.uk" must not include a scheme
                src/test/res/xml/invalid_network_security_config.xml: "invalid-port.test-example.gov.uk:443" is not a bare hostname
                src/test/res/xml/invalid_network_security_config.xml: "invalid-second-domain-config.test-example.gov.uk/" has a trailing '/'
            """.trimIndent(),
            problems.joinToString("\n"),
        )
    }

    @Test
    fun `domains are bare hostnames`() {
        val problems = configFiles.flatMap(::findProblems)
        assertTrue(
            problems.isEmpty(),
            problems.joinToString("\n"),
        )
    }

    private fun findProblems(file: File) = domainsIn(file).mapNotNull { domain ->
        when {
            domain.endsWith("/") -> "$file: \"$domain\" has a trailing '/'"
            "://" in domain -> "$file: \"$domain\" must not include a scheme"
            !hostname.matches(domain) -> "$file: \"$domain\" is not a bare hostname"
            else -> null
        }
    }

    private fun domainsIn(file: File): List<String> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = doc.getElementsByTagName("domain") // exact match
        return (0 until nodes.length).map { nodes.item(it).textContent.trim() }
    }
}
