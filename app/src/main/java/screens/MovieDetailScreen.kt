package screens

import Components.YouTubeWebView
import android.content.Intent
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.moviemuse.R
import com.example.moviemuse.VideoPlayerActivity
import android.content.pm.PackageManager
import android.widget.Toast
import android.net.Uri


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    movieId: Int,
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel()
) {
    val context = LocalContext.current // ✅ Correct placement inside @Composable function

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
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Movie Poster
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(8.dp)
                    ) {
                        Image(
                            painter = rememberImagePainter(currentMovie.posterPath),
                            contentDescription = currentMovie.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        if (!youtubeVideoId.isNullOrEmpty()) {
                            Button(
                                onClick = {
                                    val intent = Intent(context, VideoPlayerActivity::class.java)
                                    intent.putExtra("VIDEO_KEY", youtubeVideoId)
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(stringResource(id = R.string.trailer))
                            }
                        }
                    }

                    Text(
                        text = currentMovie.title,
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = currentMovie.overview,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Reviews Section
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
                        ReviewItem(review)
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Open Major Cineplex App Button
                    Button(
                        onClick = {
                            val packageName = "com.hlpth.majorcineplex"
                            val intent = Intent()
                            intent.setClassName(packageName, "com.hlpth.majorcineplex.ui.main.MainActivity")
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

                            try {
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                Toast.makeText(context, "Could not open Major Cineplex app", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Open Major Cineplex App")
                    }





                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}
