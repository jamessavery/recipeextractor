package com.example.recipeextractor

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.recipeextractor.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        viewModel = MainViewModel()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupUi()
        setupObservers()
    }

    private fun setupUi() {
        binding.disclaimer.setOnClickListener {
            val cookedWikiUrl = Uri.parse(Constants.COOKED_URL)
            openSystemBrowser(cookedWikiUrl)
        }
        binding.reOpenButton.setOnClickListener {
            val cookedWikiUrl = Uri.parse("${Constants.COOKED_URL}${viewModel.mostRecentUrl}")
            openSystemBrowser(cookedWikiUrl)
        }
    }

    private fun setupObservers() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.event.collect {
                    when (it) {
                        is MainViewModel.Event.SuccessBrowserEvent -> openSystemBrowser(it.parsedUri)
                        is MainViewModel.Event.ToastEvent -> processToastEvents(it.text)
                        is MainViewModel.Event.AlreadyVisitedUrlEvent -> setUrlAlreadyVisited(it.isVisited)
                    }
                }
            }
        }
    }

    private fun setUrlAlreadyVisited(isVisited: Boolean) {
        if(isVisited) {
            binding.reOpenButton.visibility = View.VISIBLE
        } else {
            binding.reOpenButton.visibility = View.GONE
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
            viewModel.extractValidUrl(copiedText)
        }
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

        viewModel.mostRecentUrl = parsedUrl.toString()
        startActivity(targetIntent)
    }

    private fun processToastEvents(text: String?) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }

}