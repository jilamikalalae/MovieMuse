package viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.moviemuse.model.Movie
import com.example.moviemuse.model.UserData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class UserViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _userData = MutableStateFlow<UserData?>(null)
    val userData: StateFlow<UserData?> = _userData

    // Update favorites: Firestore may store numeric values as Longs, so we convert them to Ints.
    private val _userFavorites = MutableStateFlow<List<Int>>(emptyList())
    val userFavorites: StateFlow<List<Int>> = _userFavorites

    init {
        fetchUserData()
    }

    private fun fetchUserData() {
        val userId = auth.currentUser?.uid ?: return
        db.collection("users").document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("UserViewModel", "Error fetching user data", error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    val name = snapshot.getString("name") ?: ""
                    val username = snapshot.getString("username") ?: ""
                    val email = snapshot.getString("email") ?: ""
                    _userData.value = UserData(name, email, username)

                    // Retrieve favorites field and convert to a list of Ints.
                    val favsLong = snapshot.get("favorites") as? List<Long> ?: emptyList()
                    _userFavorites.value = favsLong.map { it.toInt() }
                }
            }
    }

    fun updateProfile(name: String, username: String, onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val updates = mapOf(
            "name" to name,
            "username" to username
        )
        db.collection("users").document(userId)
            .update(updates)
            .addOnSuccessListener {
                Log.d("UserViewModel", "Profile updated successfully")
                onResult(true)
            }
            .addOnFailureListener { e ->
                Log.e("UserViewModel", "Error updating profile", e)
                onResult(false)
            }
    }

    fun toggleFavorite(movie: Movie) {
        val userId = auth.currentUser?.uid ?: return
        val userDocRef = db.collection("users").document(userId)
        val currentFavs = _userFavorites.value

        if (currentFavs.contains(movie.id)) {
            userDocRef.update("favorites", FieldValue.arrayRemove(movie.id))
                .addOnSuccessListener {
                    Log.d("UserViewModel", "Removed favorite: ${movie.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("UserViewModel", "Error removing favorite", e)
                }
        } else {
            userDocRef.update("favorites", FieldValue.arrayUnion(movie.id))
                .addOnSuccessListener {
                    Log.d("UserViewModel", "Added favorite: ${movie.id}")
                }
                .addOnFailureListener { e ->
                    Log.e("UserViewModel", "Error adding favorite", e)
                }
        }
    }
}
