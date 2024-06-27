package com.example.recipeextractor

import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel() : ViewModel() {

    // TODO Add API endpoint which scraps net for recipes so this tool removes ads -
    // 1) https://www.google.com/search?q=api+endpoint+which+returns+recipes+URLs+reddit&sca_esv=6182fbdd58dbe34a&sca_upv=1&sxsrf=ADLYWIKMCmp0nyg5Xsdk5uFO1AcpyjRcGQ%3A1719251412995&ei=1LF5Zqa1PIC5hbIP0roC&ved=0ahUKEwim74DO5vSGAxWAXEEAHVKdAAAQ4dUDCBA&uact=5&oq=api+endpoint+which+returns+recipes+URLs+reddit&gs_lp=Egxnd3Mtd2l6LXNlcnAiLmFwaSBlbmRwb2ludCB3aGljaCByZXR1cm5zIHJlY2lwZXMgVVJMcyByZWRkaXQyCBAAGIAEGKIEMggQABiABBiiBDIIEAAYogQYiQUyCBAAGIAEGKIESN8LUMkBWMcIcAJ4AZABAZgBqQGgAbsEqgEDNi4xuAEDyAEA-AEBmAIHoALWAsICChAAGLADGNYEGEfCAgoQIRigARjDBBgKmAMAiAYBkAYIkgcBN6AHpxo&sclient=gws-wiz-serp
    // 2) https://api2.bigoven.com/ , which is enabled if success response & works
    // Note - requires api key so hide feature behind feature flag
    // TODO After above ^ prac w centralised error handling - https://medium.com/@mihodihasan/android-centralized-error-handling-in-network-calls-8024a4f5f721

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    val event: SharedFlow<Event> = _event

    var mostRecentUrl: String = ""

    fun extractValidUrl(copiedText: String) {
        if (URLUtil.isValidUrl(copiedText)) {
            onValidUrl(copiedText)
        } else {
            viewModelScope.launch { _event.emit(Event.ToastEvent("Invalid URL")) }
        }
    }

    // Parses validated URL /w cooked.wiki
    // https://medium.com/asos-techblog/a-rundown-of-android-intent-selectors-youre-building-intents-wrong-fdb8d3e58ce2
    private fun onValidUrl(validatedText: String) {
        if (isUrlAlreadyVisited(validatedText)) {
            viewModelScope.launch { _event.emit(Event.AlreadyVisitedUrlEvent(true)) }
            println("Returning from viewing recipe")
            return
        } else {
            viewModelScope.launch { _event.emit(Event.AlreadyVisitedUrlEvent(false)) }
        }

        val parsedUri = Uri.parse("${Constants.COOKED_URL}$validatedText")

        viewModelScope.launch {
            _event.emit(Event.ToastEvent(parsedUri.toString()))
            _event.emit(Event.SuccessBrowserEvent(parsedUri))
        }
    }

    private fun isUrlAlreadyVisited(validatedText: String) = "${Constants.COOKED_URL}$validatedText" == mostRecentUrl

    sealed class Event() {
        data class SuccessBrowserEvent(val parsedUri: Uri) : Event()
        data class AlreadyVisitedUrlEvent(val isVisited: Boolean) : Event()
        data class ToastEvent(val text: String? = null) : Event()
    }

}