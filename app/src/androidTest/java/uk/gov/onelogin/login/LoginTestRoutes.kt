package uk.gov.onelogin.login

/**
 * Duplicates [LoginGraphObject] so that if production code changes, this also requires changing.
 */
object LoginTestRoutes {
    private const val ROOT: String = "/login"
    const val START: String = "$ROOT/start"
    const val LOADING: String = "$ROOT/loading"
}
