package screens

import Components.MoviePosterCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.moviemuse.viewmodel.MovieViewModel
import viewmodel.UserViewModel
import com.example.moviemuse.model.Movie
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.moviemuse.R
import com.example.moviemuse.components.MovieFilter

@Composable
fun MovieListScreen(
    navController: NavHostController,
    movieViewModel: MovieViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel()
) {
    val movies by movieViewModel.movies.collectAsState(initial = emptyList())
    val userFavorites by userViewModel.userFavorites.collectAsState(initial = emptyList())
    val selectedGenre by movieViewModel.selectedGenre.collectAsState(initial = null)

    val filteredMovies = if (selectedGenre != null) {
        movies.filter { it.genre.equals(selectedGenre, ignoreCase = true) }
    } else {
        movies
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {
        MovieFilter(
            selectedGenre = selectedGenre,
            genres = listOf("Sci-Fi", "Romance", "Action", "Anime"),
            onGenreSelected = movieViewModel::setGenre
        )

        MovieCategorySection(
            title = stringResource(id = R.string.trending_now),
            movies = filteredMovies.take(10),
            navController = navController,
            viewModel = userViewModel,
            userFavorites = userFavorites
        )
        MovieCategorySection(
            title = stringResource(id = R.string.new_release),
            movies = filteredMovies.takeLast(10),
            navController = navController,
            viewModel = userViewModel,
            userFavorites = userFavorites
        )
        MovieCategorySection(
            title = stringResource(id = R.string.anime),
            movies = filteredMovies.filter { it.title.contains("Anime", ignoreCase = true) },
            navController = navController,
            viewModel = userViewModel,
            userFavorites = userFavorites
        )
    }
}

@Composable
fun MovieCategorySection(
    title: String,
    movies: List<Movie>,
    navController: NavHostController,
    viewModel: UserViewModel,
    userFavorites: List<Int>
) {
    if (movies.isNotEmpty()) {
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
                        onFavoriteToggle = { viewModel.toggleFavorite(movie.id) }
                    )
                }
            }
        }
    }
}
