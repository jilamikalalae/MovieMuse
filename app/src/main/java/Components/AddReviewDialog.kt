// AddReviewDialog.kt
package com.example.moviemuse.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Write a Review") },
        containerColor = Color(0xFF1E1E1E),
        titleContentColor = Color.White,
        textContentColor = Color.White,
        text = {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { rating = index + 1 }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star ${index + 1}",
                                tint = if (index < rating) Color.Yellow else Color.Gray
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text("Share your thoughts...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color(0xFF2E2E2E),
                        focusedContainerColor = Color(0xFF2E2E2E)
                    )
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSubmit(reviewText, rating)
                    onDismiss()
                }
            ) {
                Text("Post", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}