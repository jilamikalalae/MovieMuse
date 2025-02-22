package screens

import Components.YouTubeWebView
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberImagePainter
import com.example.moviemuse.components.ReviewItem
import com.example.moviemuse.viewmodel.MovieViewModel
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.moviemuse.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel()
) {
    val movie by viewModel.getMovieById(movieId).collectAsState(initial = null)

    LaunchedEffect(movieId) {
        viewModel.getReviews(movieId)
        viewModel.getTrailers(movieId)
    }

    val reviews by viewModel.reviews.collectAsState()
    val trailers by viewModel.trailers.collectAsState()

    val youtubeVideoId = trailers.firstOrNull()?.key

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.movie_detail)) },
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
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            movie?.let { currentMovie ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    // Movie poster image
                    Image(
                        painter = rememberImagePainter(currentMovie.posterPath),
                        contentDescription = currentMovie.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(8.dp)
                    )

                    // Movie details
                    Text(
                        text = currentMovie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "⭐ ${currentMovie.rating}/10",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.Yellow
                    )
                    Text(
                        text = currentMovie.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    // Trailer section
//                    if (!youtubeVideoId.isNullOrEmpty()) {
//                        Spacer(modifier = Modifier.height(16.dp))
//                        YouTubeWebView(videoId = youtubeVideoId)
//                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reviews section
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(id = R.string.reviews),
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Button(
                            onClick = { navController.navigate("movieReviews/${currentMovie.id}") },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.secondary,
                                contentColor = MaterialTheme.colorScheme.onSecondary
                            )
                        ) {
                            Text(stringResource(id = R.string.see_all_reviews))
                        }
                    }

                    reviews.take(3).forEach { review ->
                        Spacer(modifier = Modifier.height(16.dp))
                        ReviewItem(
                            review
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}


