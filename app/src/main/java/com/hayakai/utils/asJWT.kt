package com.hayakai.utils


fun String.asJWT() = "Bearer $this"