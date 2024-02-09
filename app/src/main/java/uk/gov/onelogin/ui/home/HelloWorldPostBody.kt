package uk.gov.onelogin.ui.home

import kotlinx.serialization.Serializable

@Serializable
data class HelloWorldPostBody(val name: String, val deviceToken: String)
