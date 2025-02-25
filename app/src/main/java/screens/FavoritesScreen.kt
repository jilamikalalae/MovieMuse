package screens

import Components.MoviePosterCard
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.moviemuse.viewmodel.MovieViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviemuse.R
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue

@Composable
fun FavoritesScreen(
    navController: NavHostController,
    viewModel: MovieViewModel = viewModel()
) {
    val movies by viewModel.movies.collectAsState(initial = emptyList())
    val userFavorites by viewModel.userFavorites.collectAsState(initial = emptyList())

    // Filter movies that are favorites
    val favoriteMovies = movies.filter { movie ->
        userFavorites.contains(movie.id)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.favorites),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (favoriteMovies.isEmpty()) {
            Text(
                text = stringResource(id = R.string.no_favorite_movies),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3), // 3 items per row
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(favoriteMovies) { movie ->
                    MoviePosterCard(
                        movie = movie,
                        navController = navController,
                        isFavorite = true,
                        onFavoriteToggle = { viewModel.toggleFavorite(movie) }
                    )
                }
            }
        }
    }
}