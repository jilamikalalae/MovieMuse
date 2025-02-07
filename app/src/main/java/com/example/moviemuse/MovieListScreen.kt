package com.example.moviemuse

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*  // Ensure you're using Material 3
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import viewmodel.MovieViewModel
import com.example.moviemues.model.Movie
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun MovieListScreen(viewModel: MovieViewModel = viewModel()) {
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
        MovieCategorySection(title = "Trending Now", movies = trendingMovies)
        MovieCategorySection(title = "New Release", movies = newReleaseMovies)
        MovieCategorySection(title = "Anime", movies = animeMovies)
    }
}
@Composable
fun MovieCategorySection(title: String, movies: List<Movie>) {
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
                MoviePosterCard(movie)
            }
        }
    }
}

@Composable
fun MoviePosterCard(movie: Movie) {
    Card(
        modifier = Modifier
            .width(120.dp)   // Adjust width to fit more posters horizontally
            .height(180.dp), // Adjust height for poster ratio
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