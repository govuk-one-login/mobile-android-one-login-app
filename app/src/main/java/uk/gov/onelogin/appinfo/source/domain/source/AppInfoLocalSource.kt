package uk.gov.onelogin.appinfo.source.domain.source

import uk.gov.onelogin.appinfo.source.domain.model.AppInfoLocalState

interface AppInfoLocalSource {
    fun get(): AppInfoLocalState
    fun update(value: String)
}
