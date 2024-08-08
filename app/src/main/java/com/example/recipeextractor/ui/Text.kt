package com.example.recipeextractor.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(text: String) = Text(fontSize = 25.sp, text = text, textAlign = TextAlign.Center)

@Composable
fun InstructionsText(text: String) =
    Text(fontSize = 20.sp, textAlign = TextAlign.Center, text = text)

@Composable
fun DisclaimerText(text: String, modifier: Modifier) =
    Text(
        fontSize = 15.sp,
        textAlign = TextAlign.Center,
        text = text, lineHeight = 14.sp,
        color = Color.Gray,
        modifier = modifier
    )

@Composable
fun UrlItem(url: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { /* TODO open new screen */ }
    ) {
        Text(
            text = url,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(16.dp)
        )
    }
}