package uk.gov.onelogin.appinfo

import javax.inject.Inject

/**
 * Interface for utilities in [AppInfoService]
 */
interface AppInfoUtils {
    /**
     * Converts [String] version into [List] of [Int] to allow for comparison between versions.
     * @throws [AppError.IncorrectVersionFormat] when version does not adhere to conventional commits versioning (e.g.: 1.2.0).
     * @throws [AppError.IllegalVersionFormat] when version contains illegal elements such as extra letters. The only letter accepted is "v" preceding the version (e.g.: v1.2.0)
     */
    fun getComparableAppVersion(version: String): List<Int>

    sealed class AppError(msg: String) : Exception(msg) {
        data object IncorrectVersionFormat : AppError(
            "Version number does not adhere to Major.minor.patch convention."
        ) {
            private fun readResolve(): Any = IncorrectVersionFormat
        }

        data object IllegalVersionFormat : AppError(
            "The version contains illegal characters not adhering to the standard convention."
        ) {
            private fun readResolve(): Any = IllegalVersionFormat
        }
    }
}

class AppInfoUtilsImpl @Inject constructor() : AppInfoUtils {
    override fun getComparableAppVersion(version: String): List<Int> {
        val result = try {
            version
                // If version adheres to vM.m.p - remove "v" to allow conversion to Int
                .replace("v", "")
                .split(".").map { it.toInt() }
        } catch (e: NumberFormatException) {
            throw AppInfoUtils.AppError.IllegalVersionFormat
        }
        return if (result.size != 3) {
            throw AppInfoUtils.AppError.IncorrectVersionFormat
        } else {
            result
        }
    }
}
