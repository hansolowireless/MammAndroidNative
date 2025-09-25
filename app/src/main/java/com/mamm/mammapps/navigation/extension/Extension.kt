package com.mamm.mammapps.navigation.extension

import androidx.lifecycle.SavedStateHandle
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.model.ContentEntityUI

fun SavedStateHandle.addContent(content: Any) {
    this["content"] = content
}

fun SavedStateHandle.addRoute(route: AppRoute) {
    this["route"] = route
}