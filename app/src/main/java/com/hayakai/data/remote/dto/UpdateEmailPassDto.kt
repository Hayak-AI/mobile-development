package com.hayakai.data.remote.dto

class UpdateEmailPassDto(
    var email: String,
    var oldPassword: String? = null,
    var newPassword: String? = null
)