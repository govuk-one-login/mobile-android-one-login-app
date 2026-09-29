package uk.gov.onelogin.core.ui.dialog

import androidx.compose.ui.window.DialogProperties

val fullScreenDialogProperties =
    DialogProperties(
        usePlatformDefaultWidth = false,
        // Dialog destinations don't allow us to intercept this
        // dismiss request and it shouldn't be possible from a
        // full screen dialog anyway, so disable it.
        dismissOnClickOutside = false,
        // Display edge-to-edge
        decorFitsSystemWindows = false,
    )
