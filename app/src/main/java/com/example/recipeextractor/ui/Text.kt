package com.example.recipeextractor.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun TitleText(text: String) = Text(fontSize = 30.sp, text = text)

@Composable
fun InstructionsText(text: String) = Text(fontSize = 20.sp, textAlign = TextAlign.Center, text = text)

@Composable
fun DisclaimerText(text: String) = Text(fontSize = 15.sp, textAlign = TextAlign.Center, text = text)