package com.example.moviemuse

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.moviemuse.ui.theme.MovieMuseTheme
import com.example.moviemuse.screens.LoginScreen
import com.example.moviemuse.screens.RegisterScreen
import com.example.moviemuse.screens.ProfileScreen
import com.example.moviemuse.screens.ReviewScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import screens.MovieDetailScreen
import screens.MovieListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            var isThaiLanguage by rememberSaveable { mutableStateOf(false) }

            MovieMuseTheme {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    isThaiLanguage = isThaiLanguage,
                    onLanguageToggle = { isThaiLanguage = !isThaiLanguage }
                )
            }
        }
    }
}

@Composable
fun MainScreen(
    navController: NavHostController,
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    isThaiLanguage: Boolean,
    onLanguageToggle: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val configuration = LocalConfiguration.current
    val halfScreenWidth = (configuration.screenWidthDp.dp * 0.7f)

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(halfScreenWidth)
                    .background(Color.Gray)
            ) {
                DrawerContent(
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = onThemeToggle,
                    isThaiLanguage = isThaiLanguage,
                    onLanguageToggle = onLanguageToggle,
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                if (currentRoute != "login" && currentRoute != "register") {
                    MyTopAppBar(scope, drawerState)
                }
            },
            bottomBar = {
                if (currentRoute in listOf("home", "favorites", "search", "profile")) {
                    BottomNavBar(navController)
                }
            }
        ) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = "login",
                modifier = Modifier.padding(innerPadding)
            ) {
                composable("login") { LoginScreen(navController) }
                composable("register") { RegisterScreen(navController) }
                composable("home") { MovieListScreen(navController) }
                composable("favorites") { FavoritesScreen() }
                composable("search") { SearchScreen() }
                composable("profile") { ProfileScreen(navController) }
                composable(
                    "movieDetail/{movieId}",
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                    MovieDetailScreen(movieId, navController)
                }
                composable(
                    "movieReviews/{movieId}",
                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
                ) { backStackEntry ->
                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
                    ReviewScreen(movieId = movieId, navController = navController)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(scope:  CoroutineScope, drawerState: DrawerState) {
    TopAppBar(
        title = { Text(text = "MOVIEMUSE") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch { drawerState.open() }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Open drawer")
            }
        },
    )
}

@Composable
fun DrawerContent(
    isDarkTheme: Boolean,
    onThemeToggle: () -> Unit,
    isThaiLanguage: Boolean,
    onLanguageToggle: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        // Day/Night Mode Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onThemeToggle()
                }
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Day/Night Mode", modifier = Modifier.weight(1f))
            Switch(
                checked = isDarkTheme,
                onCheckedChange = {
                    onThemeToggle()
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thai/English Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onLanguageToggle()
                }
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Thai/English", modifier = Modifier.weight(1f))
            Switch(
                checked = isThaiLanguage,
                onCheckedChange = {
                    onLanguageToggle()
                }
            )
        }
    }
}


@Composable
fun BottomNavBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = currentRoute == "favorites",
            onClick = {
                navController.navigate("favorites") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            label = { Text("Search") },
            selected = currentRoute == "search",
            onClick = {
                navController.navigate("search") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
    }
}

@Composable
fun FavoritesScreen() {
    Text(
        text = "Favorites",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun SearchScreen() {
    Text(
        text = "Search",
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(16.dp)
    )
}