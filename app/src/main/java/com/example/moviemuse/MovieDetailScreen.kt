package com.example.moviemuse

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.example.moviemuse.model.Movie
import com.example.moviemuse.model.Review

@Composable
fun MovieDetailScreen(movieId: Int, navController: NavHostController, viewModel: MovieViewModel = viewModel()) {
    val movie by viewModel.getMovieById(movieId).collectAsState(initial = null)
    val reviews by viewModel.getReviews(movieId).collectAsState(initial = emptyList())

    movie?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Image(
                painter = rememberImagePainter(it.posterPath),
                contentDescription = it.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(8.dp)
            )

            Text(text = it.title, style = MaterialTheme.typography.headlineMedium, color = Color.White)
            Text(text = "⭐ ${it.rating}/10", style = MaterialTheme.typography.titleMedium, color = Color.Yellow)
            Text(text = it.overview, style = MaterialTheme.typography.bodyLarge, color = Color.White)

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
                    onClick = { navController.navigate("movieReviews/${movie!!.id}") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text("See All Reviews")
                }
            }

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