package com.example.recipeextractor

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.recipeextractor.ui.DisclaimerText
import com.example.recipeextractor.ui.InstructionsText
import com.example.recipeextractor.ui.TitleText
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()

        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                InstructionsScreen()
            }
        }

        setupObservers()
    }

    @Preview(showBackground = true)
    @Composable
    fun InstructionsScreen(
        modifier: Modifier = Modifier,
    ) {
        Column(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(top = 50.dp)
            ) {
                TitleText(text = stringResource(id = R.string.instructions_title))
                InstructionsText(text = stringResource(id = R.string.instructions))
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = modifier.padding(bottom = 30.dp)
            ) {
                DisclaimerText(text = stringResource(id = R.string.disclaimer))
            }
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

    // TODO replace with recipe history LazyColumn
    private fun setUrlAlreadyVisited(isVisited: Boolean) {
//        if (isVisited) {
//            binding.reOpenButton.visibility = View.VISIBLE
//        } else {
//            binding.reOpenButton.visibility = View.GONE
//        }
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