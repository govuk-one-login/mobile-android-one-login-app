package uk.gov.extensions

import java.util.Locale

object StringExtensions {
    private val kebabRegex = "-[a-zA-Z]".toRegex()
    private val snakeRegex = "_[a-zA-Z]".toRegex()
    private val proseRegex = " [a-zA-Z]".toRegex()

    fun String.kebabToLowerCamelCase(): String = kebabRegex.replace(this) {
        it.value.replace("-", "")
            .capitaliseFirstCharacter()
    }

    private fun String.proseToLowerCamelCase(): String = proseRegex.replace(this) {
        it.value.replace(" ", "")
            .capitaliseFirstCharacter()
    }

    fun String.proseToUpperCamelCase(): String = this.proseToLowerCamelCase()
        .capitaliseFirstCharacter()

    fun String.snakeToLowerCamelCase(): String = snakeRegex.replace(this) {
        it.value.replace("_", "")
            .capitaliseFirstCharacter()
    }

    private fun String.capitaliseFirstCharacter() = this.replaceFirstChar { char ->
        if (char.isLowerCase()) {
            char.titlecase(Locale.getDefault())
        } else {
            char.toString()
        }
    }
}
