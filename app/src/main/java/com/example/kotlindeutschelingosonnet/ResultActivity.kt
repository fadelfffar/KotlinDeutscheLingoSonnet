package com.example.kotlindeutschelingosonnet

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.kotlindeutschelingosonnet.ui.theme.EnglishExamTheme

class ResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val studentName = intent.getStringExtra("student_name") ?: ""
        val studentClass = intent.getStringExtra("student_class") ?: ""
        val studentId = intent.getStringExtra("student_id") ?: ""
        val score = intent.getIntExtra("score", 0)
        val totalQuestions = intent.getIntExtra("total_questions", 10)

        setContent {
            EnglishExamTheme {
                ResultScreen(
                    studentName = studentName,
                    studentClass = studentClass,
                    studentId = studentId,
                    score = score,
                    totalQuestions = totalQuestions,
                    onRetakeExam = {
                        val intent = Intent(this@ResultActivity, ExamActivity::class.java)
                        startActivity(intent)
                        finish()
                    },
                    onBackToHome = {
                        val intent = Intent(this@ResultActivity, MainActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
    }
}

@Composable
fun ResultScreen(
    studentName: String,
    studentClass: String,
    studentId: String,
    score: Int,
    totalQuestions: Int,
    onRetakeExam: () -> Unit,
    onBackToHome: () -> Unit
) {
    val percentage = (score.toDouble() / totalQuestions * 100).toInt()
    val grade = when {
        percentage >= 90 -> "A+"
        percentage >= 80 -> "A"
        percentage >= 70 -> "B"
        percentage >= 60 -> "C"
        percentage >= 50 -> "D"
        else -> "F"
    }

    val gradeColor = when (grade) {
        "A+", "A" -> Color(0xFF4CAF50)
        "B" -> Color(0xFF2196F3)
        "C" -> Color(0xFFFF9800)
        "D" -> Color(0xFFFF5722)
        else -> Color(0xFFF44336)
    }

    val message = when (grade) {
        "A+", "A" -> "🎉 Excellent work! Outstanding performance!"
        "B" -> "👏 Good job! Well done!"
        "C" -> "👍 Fair performance. Keep practicing!"
        "D" -> "📚 You passed, but there's room for improvement."
        else -> "💪 Don't give up! Study more and try again."
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(32.dp))

            // Results Header
            Text(
                text = "Exam Results",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Student Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = "Student Information",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    ResultRow("Name", studentName)
                    ResultRow("Class", studentClass)
                    if (studentId.isNotBlank()) {
                        ResultRow("Student ID", studentId)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Score Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = gradeColor.copy(alpha = 0.1f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Your Grade",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = grade,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = gradeColor
                    )

                    Text(
                        text = "$percentage%",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = gradeColor
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$score out of $totalQuestions correct",
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Message Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text(
                    text = message,
                    modifier = Modifier.padding(20.dp),
                    fontSize = 16.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Buttons
            Button(
                onClick = onRetakeExam,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Retake Exam",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBackToHome,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = "Back to Home",
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
private fun ResultRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "$label:",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}