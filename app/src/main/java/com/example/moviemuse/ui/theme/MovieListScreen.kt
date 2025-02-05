package com.example.moviemuse.ui.theme

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.moviemues.ui.components.MovieCard
import viewmodel.MovieViewModel
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier // Added this import

@Composable
fun MovieListScreen(viewModel: MovieViewModel = viewModel()) {
    // Collect the movies list as state
    val movies by viewModel.movies.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text(text = "Popular Movies", style = MaterialTheme.typography.h5)
        LazyColumn {
            items(movies) { movie ->
                MovieCard(movie)
            }
        }
    }
}
