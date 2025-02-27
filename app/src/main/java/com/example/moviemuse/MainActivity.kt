package com.example.moviemuse

import android.app.Activity
import android.app.LocaleManager
import android.content.Context
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import screens.FavoritesScreen
import screens.SearchScreen
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.example.moviemuse.utils.authenticateUser
import androidx.compose.ui.platform.LocalContext
import com.example.moviemuse.utils.authenticateUser
import android.content.ContextWrapper
import androidx.fragment.app.FragmentActivity

class MainActivity : FragmentActivity() {
    override fun attachBaseContext(newBase: Context?) {

        val context = newBase?.let { com.example.moviemuse.LocaleManager.setLocale(it) }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current

            var isDarkTheme by rememberSaveable { mutableStateOf(false) }
            var isThaiLanguage by rememberSaveable { mutableStateOf(false) }

            MovieMuseTheme(darkTheme = isDarkTheme) {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    isDarkTheme = isDarkTheme,
                    onThemeToggle = { isDarkTheme = !isDarkTheme },
                    isThaiLanguage = isThaiLanguage,
                    onLanguageToggle = {
                        val newLanguageCode = if (isThaiLanguage) "en" else "th"
                        com.example.moviemuse.LocaleManager.saveLanguage(context, newLanguageCode)
                        isThaiLanguage = !isThaiLanguage
                        (context as? Activity)?.recreate()
                    }
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
    val context = LocalContext.current

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
                    .background(MaterialTheme.colorScheme.surface)
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
                    BottomNavBar(navController, context)
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
                composable("favorites") { FavoritesScreen(navController) }
                composable("search") { SearchScreen(navController) }
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
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.height(24.dp))

        // Night Mode Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onThemeToggle() }
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.night_mode),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = { onThemeToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Thai Language Toggle
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLanguageToggle() }
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = stringResource(id = R.string.language),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = isThaiLanguage,
                onCheckedChange = { onLanguageToggle() },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.colorScheme.primary,
                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

fun getFragmentActivity(context: Context): FragmentActivity? {
    var ctx = context
    while (ctx is ContextWrapper) {
        if (ctx is FragmentActivity) return ctx  // ✅ Ensure we get a FragmentActivity
        ctx = ctx.baseContext
    }
    return null
}


@Composable
fun BottomNavBar(navController: NavHostController, context: Context) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val context = LocalContext.current  // ✅ Retrieve context inside a Composable
    val activity = getFragmentActivity(context)


    NavigationBar {
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = stringResource(id = R.string.home)
                )
            },
            label = { Text(stringResource(id = R.string.home)) },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = stringResource(id = R.string.favorites)
                )
            },
            label = { Text(stringResource(id = R.string.favorites)) },
            selected = currentRoute == "favorites",
            onClick = {
                navController.navigate("favorites") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
        NavigationBarItem(
            icon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = stringResource(id = R.string.search)
                )
            },
            label = { Text(stringResource(id = R.string.search)) },
            selected = currentRoute == "search",
            onClick = {
                navController.navigate("search") {
                    launchSingleTop = true
                    popUpTo("home") { saveState = true }
                }
            }
        )
        NavigationBarItem(
            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
            label = { Text("Profile") },
            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "profile",
            onClick = {
                if (activity != null) {
                    authenticateUser(
                        context = activity,
                        onAuthenticated = {
                            navController.navigate("profile") {
                                launchSingleTop = true
                                popUpTo("home") { saveState = true }
                            }
                        },
                        onFailed = {
                            Toast.makeText(activity, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
                        }
                    )
                } else {
                    Toast.makeText(context, "Error: Unable to get activity context", Toast.LENGTH_SHORT).show()
                }
            }
        )


    }
}