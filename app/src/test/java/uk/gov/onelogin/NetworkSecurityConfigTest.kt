package uk.gov.onelogin

import java.io.File
import javax.xml.parsers.DocumentBuilderFactory
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
    fun `domains are bare hostnames`() {
        val problems = configFiles.flatMap { file ->
            domainsIn(file).mapNotNull { domain ->
                when {
                    domain.endsWith("/") -> "$file: \"$domain\" has a trailing '/'"
                    "://" in domain -> "$file: \"$domain\" must not include a scheme"
                    !hostname.matches(domain) -> "$file: \"$domain\" is not a bare hostname"
                    else -> null
                }
            }
        }
        assertTrue(
            problems.isEmpty(),
            problems.joinToString("\n"),
        )
    }

    private fun domainsIn(file: File): List<String> {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file)
        val nodes = doc.getElementsByTagName("domain") // exact match
        return (0 until nodes.length).map { nodes.item(it).textContent.trim() }
    }
}
