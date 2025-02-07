package com.example.moviemues.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext // <-- Import LocalContext
import com.example.moviemues.model.Movie

@Composable
fun MovieCard(movie: Movie, modifier: Modifier = Modifier) {
    val context = LocalContext.current // <-- Get the context

    Card(
        modifier = modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(200.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = 4.dp
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = ImageRequest.Builder(context) // <-- Explicitly pass context here
                    .data("https://image.tmdb.org/t/p/w500${movie.posterPath}") // Use posterPath
                    .crossfade(true) // Optional: Enables crossfade animation
                    .build(),
                contentDescription = movie.title,
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp)),
                contentScale = ContentScale.Crop
            )
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .padding(8.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = movie.title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = "Rating: ${movie.rating}",
                    fontSize = 14.sp,
                    color = Color.Gray
                )
                Button(
                    onClick = { /* Handle favorite action */ },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = "Add to Watchlist")
                }
            }
        }
    }
}

