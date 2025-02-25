package screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*  // Ensure you're using Material 3
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviemuse.viewmodel.MovieViewModel
import com.example.moviemuse.model.Movie
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import androidx.compose.foundation.clickable
import androidx.navigation.NavHostController

@Composable
fun MovieListScreen(navController: NavHostController, viewModel: MovieViewModel = viewModel()) {
    val movies by viewModel.movies.collectAsState(initial = emptyList())

    // Filter movies for each category (you can adjust this logic)
    val trendingMovies = movies.take(10)   // First 5 movies for Trending
    val newReleaseMovies = movies.takeLast(10)  // Last 5 movies for New Release
    val animeMovies = movies.filter { it.title.contains("Anime", ignoreCase = true) }  // Dummy filter for Anime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
            .background(Color.Black)
    ) {
        MovieCategorySection("Trending Now", trendingMovies, navController)
        MovieCategorySection("New Release", newReleaseMovies, navController)
        MovieCategorySection("Anime", animeMovies, navController)
    }
}
@Composable
fun MovieCategorySection(title: String, movies: List<Movie>, navController: NavHostController) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = Color.White,  // White text for black background
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies) { movie ->
                MoviePosterCard(movie, navController)
            }
        }
    }
}

@Composable
fun MoviePosterCard(movie: Movie, navController: NavHostController) {
    Card(
        modifier = Modifier
            .width(120.dp)   // Adjust width to fit more posters horizontally
            .height(180.dp) // Adjust height for poster ratio
            .clickable { navController.navigate("movieDetail/${movie.id}") },
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        AsyncImage(
            model = movie.posterPath,
            contentDescription = movie.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}