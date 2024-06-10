package com.example.recipeextractor

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.webkit.URLUtil.isValidUrl
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private var mostRecentUrl: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupUi()
    }

    private fun setupUi() {
        findViewById<TextView>(R.id.disclaimer).setOnClickListener {
            val cookedWikiUrl = Uri.parse(Constants.COOKED_URL)
            openSystemBrowser(cookedWikiUrl)
        }
        findViewById<TextView>(R.id.reOpenButton).setOnClickListener {
            val cookedWikiUrl = Uri.parse("${Constants.COOKED_URL}$mostRecentUrl")
            openSystemBrowser(cookedWikiUrl)
        }
    }

    // Utilising the clipboard is only allowed when app is fully created (focused) & in the foreground
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        doExtractClipboard()
    }

    // Extracts most recent clipboard item
    private fun doExtractClipboard() {
        val clipboardManager = this.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager

        val clipData = clipboardManager.primaryClip

        val copiedText = clipData?.getItemAt(0)?.text?.toString()

        if (copiedText.isNullOrEmpty()) {
            println("Empty clipboard")
        } else {
            extractValidUrl(copiedText)
        }
    }

    private fun extractValidUrl(copiedText: String) {
        if (isValidUrl(copiedText)) {
            onValidUrl(copiedText)
        } else {
            Toast.makeText(this, "Invalid URL", Toast.LENGTH_SHORT).show()
        }
    }

    // Parses validated URL /w cooked.wiki
    // https://medium.com/asos-techblog/a-rundown-of-android-intent-selectors-youre-building-intents-wrong-fdb8d3e58ce2
    private fun onValidUrl(validatedText: String) {
        if ("${Constants.COOKED_URL}$validatedText" == mostRecentUrl) {
            findViewById<TextView>(R.id.reOpenButton).visibility = View.VISIBLE
            println("Returning from viewing recipe") // TODO state management
            return
        }

        val parsedUrl = Uri.parse("${Constants.COOKED_URL}$validatedText")
        Toast.makeText(this, parsedUrl.toString(), Toast.LENGTH_SHORT).show()

        openSystemBrowser(parsedUrl)
    }

    private fun openSystemBrowser(parsedUrl: Uri) {
        val browserSelectorIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(Uri.parse("http:"))

        val targetIntent = Intent()
            .setAction(Intent.ACTION_VIEW)
            .addCategory(Intent.CATEGORY_BROWSABLE)
            .setData(parsedUrl)

        targetIntent.selector = browserSelectorIntent

        mostRecentUrl = parsedUrl.toString()
        startActivity(targetIntent)
    }

}