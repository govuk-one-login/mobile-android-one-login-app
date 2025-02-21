package uk.gov.onelogin.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import kotlin.text.replace

const val GOV_UK_TEXT = "GOV.UK"
const val GOV_UK_CONTENT_DESCRIPTION = "Gov dot UK"

fun Modifier.customAccessibility(text: String): Modifier =
    if (text.contains(GOV_UK_TEXT)) {
        this.semantics {
            contentDescription = text.replace(GOV_UK_TEXT, GOV_UK_CONTENT_DESCRIPTION)
        }
    } else {
        this
    }