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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.moviemuse.R
import com.example.moviemuse.components.MovieHeader
import com.example.moviemuse.components.ReviewHeader
import com.example.moviemuse.components.ReviewItem
import com.example.moviemuse.model.Movie
import com.example.moviemuse.viewmodel.MovieViewModel
import com.example.moviemuse.model.Review
import viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    movieId: Int,
    navController: NavHostController,
    userViewModel: UserViewModel = viewModel(),
    viewModel: MovieViewModel = viewModel()
) {

    val userData by userViewModel.userData.collectAsState()
    val movie by viewModel.getMovieById(movieId).collectAsState(initial = null)

    // Fetch reviews once when the screen is launched
    LaunchedEffect(movieId) {
        viewModel.getReviews(movieId)
    }

    val reviews by viewModel.reviews.collectAsState()
    var showAddReviewDialog by remember { mutableStateOf(false) }

    val colors = MaterialTheme.colorScheme // Get current theme colors

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.reviews)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onSurface,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colors.background)
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
                    viewModel.addReview(content, rating, movieId, userData?.username ?: "Anonymous User")
                    showAddReviewDialog = false
                }
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddReviewDialog(
    onDismiss: () -> Unit,
    onSubmit: (String, Int) -> Unit
) {
    var reviewText by remember { mutableStateOf("") }
    var rating by remember { mutableStateOf(5) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.write_a_review)) },
        containerColor = MaterialTheme.colorScheme.surface,
        titleContentColor = MaterialTheme.colorScheme.onSurface,
        textContentColor = MaterialTheme.colorScheme.onSurface,
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
                                contentDescription = stringResource(id = R.string.star_desc, index + 1),
                                tint = if (index < rating)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                TextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    placeholder = { Text(text = stringResource(id = R.string.share_your_thoughts)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
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
                Text(
                    text = stringResource(id = R.string.post),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = stringResource(id = R.string.cancel),
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}
