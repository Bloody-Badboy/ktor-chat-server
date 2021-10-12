package dev.arpan.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
class LoginResponse(@Json(name = "token") val token: String)
