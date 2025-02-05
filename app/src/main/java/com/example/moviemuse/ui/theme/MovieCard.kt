package com.example.moviemues.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter

@Composable
fun MovieCard(movieTitle: String, posterUrl: String, rating: Float) {
    Card(
        elevation = 4.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column {
            Image(
                painter = rememberAsyncImagePainter(posterUrl),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = movieTitle,
                style = MaterialTheme.typography.h6,
                modifier = Modifier.padding(8.dp)
            )
            RatingBar(rating)
        }
    }
}

@Composable
fun RatingBar(rating: Float) {
    Row(modifier = Modifier.padding(8.dp)) {
        repeat(5) { index ->
            Icon(
                painter = painterResource(
                    if (index < rating.toInt()) android.R.drawable.star_on else android.R.drawable.star_off
                ),
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
