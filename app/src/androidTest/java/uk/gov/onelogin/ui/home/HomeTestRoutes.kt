package uk.gov.onelogin.ui.home

/**
 * Duplicates [HomeRoutes] so that if production code changes, this also requires changing.
 */
object HomeTestRoutes {
    const val ROOT: String = "/home"
    const val START: String = "$ROOT/start"
}
