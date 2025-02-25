package screens

import Components.MoviePosterCard
import android.util.Log
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.navigation.NavHostController
import com.example.moviemuse.R

@Composable
fun MovieListScreen(navController: NavHostController, viewModel: MovieViewModel = viewModel()) {
    val movies by viewModel.movies.collectAsState(initial = emptyList())
    val userFavorites by viewModel.userFavorites.collectAsState()

    val trendingMovies = movies.take(10)
    val newReleaseMovies = movies.takeLast(10)
    val animeMovies = movies.filter { it.title.contains("Anime", ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background) // Themed background
            .padding(8.dp)
    ) {
        MovieCategorySection(
            title = stringResource(id = R.string.trending_now),
            movies = trendingMovies,
            navController = navController,
            viewModel = viewModel,
            userFavorites = userFavorites
        )
        MovieCategorySection(
            title = stringResource(id = R.string.new_release),
            movies = newReleaseMovies,
            navController = navController,
            viewModel = viewModel,
            userFavorites = userFavorites
        )
        MovieCategorySection(
            title = stringResource(id = R.string.anime),
            movies = animeMovies,
            navController = navController,
            viewModel = viewModel,
            userFavorites = userFavorites
        )
    }
}

@Composable
fun MovieCategorySection(
    title: String,
    movies: List<Movie>,
    navController: NavHostController,
    viewModel: MovieViewModel,
    userFavorites: List<Int>
) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 8.dp, bottom = 8.dp)
        )

        LazyRow(
            contentPadding = PaddingValues(horizontal = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(movies) { movie ->
                MoviePosterCard(
                    movie = movie,
                    navController = navController,
                    isFavorite = userFavorites.contains(movie.id),
                    onFavoriteToggle = { viewModel.toggleFavorite(movie) }
                )
            }
        }
    }
}

