package com.hayakai.data.remote.dto

class UpdateContactDto(
    private val contact_id: Int,
    private val name: String,
    private val email: String,
    private val phone: String,
    private val notify: Boolean,
    private val message: String
)