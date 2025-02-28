package screens

import Components.MoviePosterCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*  // Ensure you're using Material 3
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviemuse.viewmodel.MovieViewModel
import com.example.moviemuse.model.Movie
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import com.example.moviemuse.R
import viewmodel.UserViewModel

@Composable
fun MovieListScreen(
    navController: NavHostController,
    movieViewModel: MovieViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val movies by movieViewModel.movies.collectAsState(initial = emptyList())
    val userFavorites by userViewModel.userFavorites.collectAsState()

    val trendingMovies = movies.take(10)
    val newReleaseMovies = movies.takeLast(10)
    val animeMovies = movies.filter { it.title.contains("Anime", ignoreCase = true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        MovieCategorySection(
            title = stringResource(id = R.string.trending_now),
            movies = trendingMovies,
            navController = navController,
            userFavorites = userFavorites,
            onFavoriteToggle = { movie -> userViewModel.toggleFavorite(movie) }
        )
        MovieCategorySection(
            title = stringResource(id = R.string.new_release),
            movies = newReleaseMovies,
            navController = navController,
            userFavorites = userFavorites,
            onFavoriteToggle = { movie -> userViewModel.toggleFavorite(movie) }
        )
        MovieCategorySection(
            title = stringResource(id = R.string.anime),
            movies = animeMovies,
            navController = navController,
            userFavorites = userFavorites,
            onFavoriteToggle = { movie -> userViewModel.toggleFavorite(movie) }
        )
    }
}

@Composable
fun MovieCategorySection(
    title: String,
    movies: List<Movie>,
    navController: NavHostController,
    userFavorites: List<Int>,
    onFavoriteToggle: (Movie) -> Unit
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
                    onFavoriteToggle = { onFavoriteToggle(movie) } // Now passed from MovieListScreen
                )
            }
        }
    }
}


