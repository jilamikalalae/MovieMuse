package screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.moviemuse.viewmodel.MovieViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel()
) {
    val movie by viewModel.getMovieById(movieId).collectAsState(initial = null)
    val reviews by viewModel.getReviews(movieId).collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Movie Detail") },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Black,      // Top bar background color
                    titleContentColor = Color.White,    // Title text color
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { innerPadding ->
        // The main content of the screen
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)    // Screen background
                .padding(innerPadding)
        ) {
            // Render your movie details if movie is not null
            movie?.let { currentMovie ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Image(
                        painter = rememberImagePainter(currentMovie.posterPath),
                        contentDescription = currentMovie.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(8.dp)
                    )

                    Text(
                        text = currentMovie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Text(
                        text = "⭐ ${currentMovie.rating}/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Yellow
                    )
                    Text(
                        text = currentMovie.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reviews",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White
                        )

                        Button(
                            onClick = { navController.navigate("movieReviews/${currentMovie.id}") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = Color.Black
                            )
                        ) {
                            Text("See All Reviews")
                        }
                    }

                    // Show only the first two reviews in the detail screen
                    reviews.take(2).forEach { review ->
                        Text(
                            text = "• ${review.author}: ${review.content}",
                            color = Color.Gray,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}