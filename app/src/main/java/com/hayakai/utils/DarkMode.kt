package com.hayakai.utils

import android.app.Activity
import androidx.fragment.app.Fragment

fun Fragment.isDarkMode(): Boolean {
    return resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
}

fun Activity.isDarkMode(): Boolean {
    return resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES
}

