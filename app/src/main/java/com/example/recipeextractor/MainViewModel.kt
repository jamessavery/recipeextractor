package com.example.recipeextractor

import android.net.Uri
import android.webkit.URLUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

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