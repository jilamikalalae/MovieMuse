package com.example.moviemuse.ui.components

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
import com.example.moviemuse.model.Movie
import androidx.compose.foundation.clickable
import coil.compose.rememberImagePainter


//@Composable
//fun MovieCard(movie: Movie, modifier: Modifier = Modifier) {
//    val context = LocalContext.current // <-- Get the context
//
//    Card(
//        modifier = modifier
//            .padding(8.dp)
//            .fillMaxWidth()
//            .height(200.dp),
//        shape = RoundedCornerShape(12.dp),
//        elevation = 4.dp
//    ) {
//        Row(modifier = Modifier.fillMaxSize()) {
//            AsyncImage(
//                model = ImageRequest.Builder(context) // <-- Explicitly pass context here
//                    .data(movie.posterPath) // Use posterPath
//                    .crossfade(true) // Optional: Enables crossfade animation
//                    .build(),
//                contentDescription = movie.title,
//                modifier = Modifier
//                    .weight(0.4f)
//                    .fillMaxHeight()
//                    .clip(RoundedCornerShape(12.dp)),
//                contentScale = ContentScale.Crop
//            )
//            Column(
//                modifier = Modifier
//                    .weight(0.6f)
//                    .padding(8.dp),
//                verticalArrangement = Arrangement.SpaceBetween
//            ) {
//                Text(
//                    text = movie.title,
//                    fontSize = 18.sp,
//                    fontWeight = FontWeight.Bold,
//                    color = Color.Black
//                )
//                Text(
//                    text = "Rating: ${movie.rating}",
//                    fontSize = 14.sp,
//                    color = Color.Gray
//                )
//                Button(
//                    onClick = { /* Handle favorite action */ },
//                    modifier = Modifier.align(Alignment.End)
//                ) {
//                    Text(text = "Add to Watchlist")
//                }
//            }
//        }
//    }
//}
@Composable
fun MovieCard(movie: Movie, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = 4.dp
    ) {
        Row(
            modifier = Modifier.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = movie.imageUrl,
                contentDescription = movie.title,
                modifier = Modifier.size(80.dp)
            )


//            Image(
//                painter = rememberImagePainter(movie.imageUrl),
//                contentDescription = movie.title,
//                modifier = Modifier.size(80.dp)
//            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = movie.title, fontWeight = FontWeight.Bold)
                Text(text = "Genre: ${movie.genre}")
            }
        }
    }
}

