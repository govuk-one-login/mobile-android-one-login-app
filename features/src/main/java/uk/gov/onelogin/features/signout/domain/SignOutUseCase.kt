package uk.gov.onelogin.features.signout.domain

fun interface SignOutUseCase {
    suspend fun invoke()
}
