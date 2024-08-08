package com.example.recipeextractor

import android.content.ClipboardManager
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.recipeextractor.ui.DisclaimerText
import com.example.recipeextractor.ui.InstructionsText
import com.example.recipeextractor.ui.TitleText
import com.example.recipeextractor.ui.UrlItem
import com.example.recipeextractor.ui.theme.RecipeExtractorTheme

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = MainViewModel()

        setContent {
            RecipeExtractorTheme {
                RecipeExtractorApp(
                    viewModel = viewModel,
                    openSystemBrowser = ::openSystemBrowser,
                    processToastEvents = ::processToastEvents,
                    setUrlAlreadyVisited = ::setUrlAlreadyVisited
                )
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

@Composable
fun RecipeExtractorApp(
    viewModel: MainViewModel,
    openSystemBrowser: (Uri) -> Unit,
    processToastEvents: (String?) -> Unit,
    setUrlAlreadyVisited: (Boolean) -> Unit
) {
    LaunchedEffect(viewModel.event) {
        viewModel.event.collect {
            when (it) {
                is MainViewModel.Event.SuccessBrowserEvent -> openSystemBrowser(it.parsedUri)
                is MainViewModel.Event.ToastEvent -> processToastEvents(it.text)
                is MainViewModel.Event.AlreadyVisitedUrlEvent -> setUrlAlreadyVisited(it.isVisited)
            }
        }
    }

    Surface(color = MaterialTheme.colorScheme.background) {
        MainScreen()
    }
}

@Preview(name = "PIXEL_3A", device = Devices.PIXEL_3A)
@Preview(name = "NEXUS_7", device = Devices.NEXUS_7)
@Preview(name = "NEXUS_7_2013", device = Devices.NEXUS_7_2013)
@Preview(name = "NEXUS_5", device = Devices.NEXUS_5)
@Preview(name = "NEXUS_6", device = Devices.NEXUS_6)
@Preview(name = "NEXUS_9", device = Devices.NEXUS_9)
@Preview(name = "NEXUS_10", device = Devices.NEXUS_10)
@Preview(name = "NEXUS_5X", device = Devices.NEXUS_5X)
@Preview(name = "NEXUS_6P", device = Devices.NEXUS_6P)
@Preview(name = "PIXEL_C", device = Devices.PIXEL_C)
@Preview(name = "PIXEL", device = Devices.PIXEL)
@Preview(name = "PIXEL_XL", device = Devices.PIXEL_XL)
@Preview(name = "PIXEL_2", device = Devices.PIXEL_2)
@Preview(name = "PIXEL_2_XL", device = Devices.PIXEL_2_XL)
@Preview(name = "PIXEL_3", device = Devices.PIXEL_3)
@Preview(name = "PIXEL_3_XL", device = Devices.PIXEL_3_XL)
@Preview(name = "PIXEL_3A", device = Devices.PIXEL_3A)
@Preview(name = "PIXEL_3A_XL", device = Devices.PIXEL_3A_XL)
@Preview(name = "PIXEL_4", device = Devices.PIXEL_4)
@Preview(name = "PIXEL_4_XL", device = Devices.PIXEL_4_XL)
@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        InstructionsContent(
            Modifier
//                .heightIn(min = 10.dp, max = 160.dp) // TODO JIMMY properly understand this, might be doing it wrong
                .weight(4f)
        )

//            HistoryContent(
//                Modifier
//                    .heightIn(min = 400.dp, max = 400.dp)
//                    .weight(6f)
//            )

        DisclaimerContent(
            Modifier
                .heightIn(min = 70.dp, max = 150.dp)
                .weight(1f)
        )
    }
}

@Composable
private fun InstructionsContent(modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize(),
    ) {
        TitleText(text = stringResource(id = R.string.instructions_title))
        InstructionsText(text = stringResource(id = R.string.instructions))
    }
}

@Preview
@Composable
private fun HistoryContent(modifier: Modifier = Modifier) {
    LazyColumn(
        modifier = modifier
            .fillMaxHeight(0.6f)
    ) {
        item {
            TitleText(text = "History")
        }
        items(
            items = listOf(
                "https://gemini.google.com/app/5dfc350591c3d9bcasd",
                "https://gemini.google.com/app/5dfc350591c3d9bc",
                "https://gemini.google.com/app/5asdasdasddfc350591c3d9bc",
                "https://gemini.google.com/app/5dfc350591casdasdasd3d9bcasd",
                "https://gemini.google.com/app/5dfc3asgasgadgdgag50591c3d9bc",
                "https://gemini.google.com/app/5asdasdasddasfasfasffc350591c3d9bc"
            ),
            key = { it }
        ) { item ->
            UrlItem(item)
        }
    }
}

// TODO merge into the history LazyColumn as a footer. Limit LC to 3 items & onClick goes to new frag
@Composable
private fun DisclaimerContent(modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.BottomCenter
    ) {
        DisclaimerText(
            text = stringResource(id = R.string.disclaimer),
            modifier = modifier
        )
    }
}