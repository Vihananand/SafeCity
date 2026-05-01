package com.example.safecity.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TrainingScreen() {
    val context = LocalContext.current
    var score by remember { mutableIntStateOf(0) }
    var currentQuestionIndex by remember { mutableIntStateOf(0) }
    
    val questions = listOf(
        Question(
            "What should you do if you feel you're being followed?",
            listOf("Run into an alley", "Go to a well-lit public place", "Stop and confront them", "Go home immediately"),
            1
        ),
        Question(
            "What is the first step in an SOS emergency?",
            listOf("Take a photo", "Call a friend", "Trigger the SOS button", "Post on social media"),
            2
        ),
        Question(
            "Where can you find 'Safe Havens' in the app?",
            listOf("On the Map screen", "In Settings", "In the Profile", "In the Community feed"),
            0
        )
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.School, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Safety Training Quiz", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            }
            Text(text = "Improve your safety knowledge and earn Guardian points.", style = MaterialTheme.typography.bodyMedium)
            
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
        }

        if (currentQuestionIndex < questions.size) {
            item {
                val question = questions[currentQuestionIndex]
                QuestionCard(
                    question = question,
                    onAnswerSelected = { isCorrect ->
                        if (isCorrect) score += 10
                        if (currentQuestionIndex < questions.size) {
                            currentQuestionIndex++
                        }
                        Toast.makeText(context, if (isCorrect) "Correct! +10 pts" else "Keep learning!", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        } else {
            item {
                ResultCard(score = score) {
                    currentQuestionIndex = 0
                    score = 0
                }
            }
        }
    }
}

data class Question(val text: String, val options: List<String>, val correctIndex: Int)

@Composable
fun QuestionCard(question: Question, onAnswerSelected: (Boolean) -> Unit) {
    var selectedOption by remember { mutableStateOf<Int?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = question.text, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            
            question.options.forEachIndexed { index, option ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .selectable(
                            selected = (selectedOption == index),
                            onClick = { selectedOption = index }
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = (selectedOption == index),
                        onClick = { selectedOption = index }
                    )
                    Text(text = option, modifier = Modifier.padding(start = 8.dp))
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = { 
                    if (selectedOption != null) {
                        onAnswerSelected(selectedOption == question.correctIndex)
                        selectedOption = null
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedOption != null
            ) {
                Text("Submit Answer")
            }
        }
    }
}

@Composable
fun ResultCard(score: Int, onRestart: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(64.dp), tint = Color(0xFF4CAF50))
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = "Training Complete!", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = "You earned $score Guardian Points", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(24.dp))
            Button(onClick = onRestart) {
                Text("Restart Quiz")
            }
        }
    }
}
