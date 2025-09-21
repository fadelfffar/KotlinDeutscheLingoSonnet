package com.example.kotlindeutschelingosonnet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.*
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlindeutschelingosonnet.data.Question
import com.example.kotlindeutschelingosonnet.data.QuestionRepository
import com.example.kotlindeutschelingosonnet.ui.theme.GermanExamTheme
import kotlinx.coroutines.delay

class ExamActivity : ComponentActivity() {
    private lateinit var studentName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        studentName = intent.getStringExtra("student_name") ?: "Schüler"

        setContent {
            GermanExamTheme {
                GermanExamScreen(
                    studentName = studentName,
                    onExamComplete = { score, totalQuestions ->
                        val intent = Intent(this@ExamActivity, ResultActivity::class.java)
                        intent.putExtra("student_name", studentName)
                        intent.putExtra("score", score)
                        intent.putExtra("total_questions", totalQuestions)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GermanExamScreen(
    studentName: String,
    onExamComplete: (Int, Int) -> Unit
) {
    val questions = remember { QuestionRepository.getGermanQuestions() }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedAnswer by remember { mutableStateOf(-1) }
    var score by remember { mutableStateOf(0) }
    var showFeedback by remember { mutableStateOf(false) }
    var isCorrect by remember { mutableStateOf(false) }
    var showTranslations by remember { mutableStateOf(true) }

    val currentQuestion = questions[currentQuestionIndex]
    val progress = (currentQuestionIndex + 1).toFloat() / questions.size

    LaunchedEffect(showFeedback) {
        if (showFeedback) {
            delay(5000)
            if (currentQuestionIndex < questions.size - 1) {
                currentQuestionIndex++
                selectedAnswer = -1
                showFeedback = false
            } else {
                onExamComplete(score, questions.size)
            }
        }
    }

    Surface(
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with translation toggle
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFFFD700)
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "🇩🇪 [translate:Hallo], $studentName!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black
                            )
                            Text(
                                text = "[translate:Deutsch-Quiz] (German Quiz)",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            Text(
                                text = "[translate:Frage] ${currentQuestionIndex + 1} [translate:von] ${questions.size}",
                                fontSize = 14.sp,
                                color = Color.Black
                            )
                            if (showTranslations) {
                                Text(
                                    text = "Question ${currentQuestionIndex + 1} of ${questions.size}",
                                    fontSize = 12.sp,
                                    color = Color.Black.copy(alpha = 0.7f),
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }

                        // Translation toggle button
                        IconButton(
                            onClick = { showTranslations = !showTranslations }
                        ) {
                            Icon(
                                imageVector = if (showTranslations) Icons.Default.Warning else Icons.Default.ThumbUp,
                                contentDescription = "Toggle translations",
                                tint = Color.Black
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = Color(0xFFDD0000),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Question Card with Translation
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                ) {
                    // Question type indicator with translation
                    if (currentQuestion.questionType.isNotEmpty()) {
                        Text(
                            text = currentQuestion.questionType,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                        if (showTranslations && currentQuestion.questionTypeTranslation.isNotEmpty()) {
                            Text(
                                text = currentQuestion.questionTypeTranslation,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        } else {
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    // Main question with translation
                    Text(
                        text = currentQuestion.questionText,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        lineHeight = 26.sp
                    )

                    if (showTranslations && currentQuestion.questionTranslation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "📝 ${currentQuestion.questionTranslation}",
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.secondary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    // Pronunciation hint
                    if (currentQuestion.pronunciation.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "🔊 ${currentQuestion.pronunciation}",
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Answer Options with translations
                    currentQuestion.options.forEachIndexed { index, option ->
                        val isSelected = selectedAnswer == index
                        val backgroundColor = when {
                            showFeedback && index == currentQuestion.correctAnswerIndex -> Color(0xFF4CAF50)
                            showFeedback && isSelected && index != currentQuestion.correctAnswerIndex -> Color(0xFFE57373)
                            isSelected -> Color(0xFFFFD700).copy(alpha = 0.3f)
                            else -> MaterialTheme.colorScheme.surface
                        }

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .selectable(
                                    selected = isSelected,
                                    onClick = {
                                        if (!showFeedback) {
                                            selectedAnswer = index
                                        }
                                    }
                                ),
                            colors = CardDefaults.cardColors(containerColor = backgroundColor),
                            elevation = if (isSelected) CardDefaults.cardElevation(defaultElevation = 8.dp)
                            else CardDefaults.cardElevation(defaultElevation = 2.dp),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(2.dp, Color(0xFFDD0000)) else null
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = "${('A' + index)} $option",
                                    fontSize = 16.sp,
                                    fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal,
                                    color = if (showFeedback && (index == currentQuestion.correctAnswerIndex ||
                                                (isSelected && index != currentQuestion.correctAnswerIndex)))
                                        Color.White
                                    else MaterialTheme.colorScheme.onSurface
                                )

                                // Show translation for options
                                if (showTranslations &&
                                    currentQuestion.optionTranslations.isNotEmpty() &&
                                    index < currentQuestion.optionTranslations.size &&
                                    currentQuestion.optionTranslations[index].isNotEmpty()) {

                                    Text(
                                        text = "→ ${currentQuestion.optionTranslations[index]}",
                                        fontSize = 14.sp,
                                        color = if (showFeedback && (index == currentQuestion.correctAnswerIndex ||
                                                    (isSelected && index != currentQuestion.correctAnswerIndex)))
                                            Color.White.copy(alpha = 0.8f)
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Feedback Display with translations
            if (showFeedback) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCorrect) Color(0xFF4CAF50) else Color(0xFFE57373)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = if (isCorrect) "✓ [translate:Richtig! Sehr gut!]" else "✗ [translate:Falsch! Nicht aufgeben!]",
                            modifier = Modifier.fillMaxWidth(),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )

                        if (showTranslations) {
                            Text(
                                text = if (isCorrect) "Correct! Very good!" else "Wrong! Don't give up!",
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                textAlign = TextAlign.Center,
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                            )
                        }

                        // Show explanation with translation
                        if (currentQuestion.explanation.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = currentQuestion.explanation,
                                modifier = Modifier.fillMaxWidth(),
                                fontSize = 14.sp,
                                color = Color.White,
                                textAlign = TextAlign.Center
                            )

                            if (showTranslations && currentQuestion.explanationTranslation.isNotEmpty()) {
                                Text(
                                    text = "📖 ${currentQuestion.explanationTranslation}",
                                    modifier = Modifier.fillMaxWidth(),
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.9f),
                                    textAlign = TextAlign.Center,
                                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Submit Button with German text and translation
            Button(
                onClick = {
                    if (selectedAnswer != -1 && !showFeedback) {
                        isCorrect = selectedAnswer == currentQuestion.correctAnswerIndex
                        if (isCorrect) score++
                        showFeedback = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = selectedAnswer != -1 && !showFeedback,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDD0000)
                )
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (currentQuestionIndex == questions.size - 1) "[translate:Quiz beenden]" else "[translate:Antwort bestätigen]",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                    if (showTranslations) {
                        Text(
                            text = if (currentQuestionIndex == questions.size - 1) "Finish Quiz" else "Confirm Answer",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.8f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}
