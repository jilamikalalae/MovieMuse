package com.example.moviemuse.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun MovieFilter(
    selectedGenre: String?,
    genres: List<String>,
    onGenreSelected: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        genres.forEach { genre ->
            FilterChip(
                selected = selectedGenre == genre,
                onClick = { onGenreSelected(if (selectedGenre == genre) null else genre) },
                label = { Text(text = genre) }
            )
        }
    }
}

class MovieFilter {

}
