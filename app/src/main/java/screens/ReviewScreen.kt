package com.example.moviemuse.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.moviemuse.model.Movie
import com.example.moviemuse.viewmodel.MovieViewModel
import com.example.moviemuse.model.Review

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    movieId: Int,
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel()
) {
    val movie by viewModel.getMovieById(movieId).collectAsState(initial = null)
    val reviews by viewModel.getReviews(movieId).collectAsState(initial = emptyList())
    var showAddReviewDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                movie?.let { currentMovie ->
                    item {
                        MovieHeader(currentMovie)
                        Spacer(modifier = Modifier.height(16.dp))
                        ReviewHeader(onAddReviewClick = { showAddReviewDialog = true })
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                items(reviews) { review ->
                    ReviewItem(review)
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }

        if (showAddReviewDialog) {
            AddReviewDialog(
                onDismiss = { showAddReviewDialog = false },
                onSubmit = { content, rating ->
                    // Call viewModel function to add review
                    showAddReviewDialog = false
                }
            )
        }
    }
}

@Composable
fun MovieHeader(movie: Movie) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = movie.posterPath,
            contentDescription = movie.title,
            modifier = Modifier
                .width(80.dp)
                .height(120.dp)
                .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.titleLarge,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            RatingBar(rating = (movie.rating / 2).toInt())
        }
    }
}

@Composable
fun ReviewHeader(onAddReviewClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Reviews",
            style = MaterialTheme.typography.titleLarge,
            color = Color.White
        )
        Button(
            onClick = onAddReviewClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            )
        ) {
            Text("Write a review")
        }
    }
}

@Composable
fun ReviewItem(review: Review) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1E1E1E)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Color.Gray)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = review.author,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Row {
                        repeat(5) { index ->
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Star",
                                tint = Color.Yellow,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = review.content,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
        }
    }
}

@Composable
fun RatingBar(rating: Int, maxStars: Int = 5) {
    Row {
        repeat(maxStars) { index ->
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = if (index < rating) Color.Yellow else Color.Gray,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

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