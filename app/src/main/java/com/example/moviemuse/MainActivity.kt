package com.example.moviemuse

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.content.res.Configuration
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import screens.*
import com.example.moviemuse.ui.theme.MovieMuseTheme
import androidx.fragment.app.FragmentActivity
import com.example.moviemuse.screens.ProfileScreen
import androidx.compose.ui.Alignment

class MainActivity : FragmentActivity() {
    override fun attachBaseContext(newBase: Context?) {
        val context = newBase?.let { com.example.moviemuse.LocaleManager.setLocale(it) }
        super.attachBaseContext(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val savedLanguage = com.example.moviemuse.LocaleManager.getSavedLanguage(this)
        val isDefaultThai = savedLanguage == "th"

        setContent {
            val context = LocalContext.current

            var isLightTheme by rememberSaveable { mutableStateOf(false) }
            var isThaiLanguage by rememberSaveable { mutableStateOf(isDefaultThai) }

            MovieMuseTheme(isLightTheme = isLightTheme) {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController,
                    isLightTheme = isLightTheme,
                    onThemeToggle = { isLightTheme = !isLightTheme },
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

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainScreen(
    navController: NavHostController,
    isLightTheme: Boolean,
    onThemeToggle: () -> Unit,
    isThaiLanguage: Boolean,
    onLanguageToggle: () -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(isLandscape) {
        if (isLandscape) drawerState.open() else drawerState.close()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                DrawerContent(isLightTheme, onThemeToggle, isThaiLanguage, onLanguageToggle)
            }
        }
    ) {
        Scaffold(
            topBar = { MyTopAppBar(scope, drawerState) },
            bottomBar = { BottomNavBar(navController) }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(navController = navController, startDestination = "home") {
                    composable("home") { MovieListScreen(navController) }
                    composable("favorites") { FavoritesScreen(navController) }
                    composable("search") { SearchScreen(navController) }
                    composable("profile") { ProfileScreen(navController) }  
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTopAppBar(scope: CoroutineScope, drawerState: DrawerState) {
    TopAppBar(
        title = { Text(text = "MOVIEMUSE") },
        navigationIcon = {
            IconButton(onClick = {
                scope.launch {
                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
                }
            }) {
                Icon(Icons.Default.Menu, contentDescription = "Open drawer")
            }
        },
    )
}

@Composable
fun DrawerContent(
    isLightTheme: Boolean,
    onThemeToggle: () -> Unit,
    isThaiLanguage: Boolean,
    onLanguageToggle: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth().clickable { onThemeToggle() }.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Dark Mode", modifier = Modifier.weight(1f))
            Switch(checked = isLightTheme, onCheckedChange = { onThemeToggle() })
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth().clickable { onLanguageToggle() }.padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "ภาษาไทย", modifier = Modifier.weight(1f))
            Switch(checked = isThaiLanguage, onCheckedChange = { onLanguageToggle() })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomNavBar(navController: NavHostController) {
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = currentRoute == "home",
            onClick = {
                navController.navigate("home") {
                    launchSingleTop = true
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
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
                    popUpTo(navController.graph.startDestinationId) { saveState = true }
                }
            }
        )
    }
}

//package com.example.moviemuse
//
//import android.annotation.SuppressLint
//import android.app.Activity
//import android.content.Context
//import android.os.Bundle
//import android.content.res.Configuration
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.layout.*
//import androidx.compose.material3.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import androidx.navigation.compose.*
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import screens.*
//import com.example.moviemuse.ui.theme.MovieMuseTheme
//import androidx.fragment.app.FragmentActivity
//import com.example.moviemuse.screens.ProfileScreen
//import androidx.compose.foundation.clickable
//import androidx.compose.ui.Alignment
//
//
//class MainActivity : FragmentActivity() {
//    override fun attachBaseContext(newBase: Context?) {
//        val context = newBase?.let { com.example.moviemuse.LocaleManager.setLocale(it) }
//        super.attachBaseContext(context)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            MovieMuseTheme {
//                val navController = rememberNavController()
//                MainScreen(navController)
//            }
//        }
//    }
//}
//
//
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
//@Composable
//fun MainScreen(navController: NavHostController) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    val configuration = LocalConfiguration.current
//    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
//
//
//    LaunchedEffect(isLandscape) {
//        if (isLandscape) {
//            drawerState.open()
//        } else {
//            drawerState.close()
//        }
//    }
//
//    ModalNavigationDrawer(
//        drawerState = drawerState,
//        drawerContent = {
//            Box(
//                modifier = Modifier
//                    .fillMaxHeight()
//                    .width(250.dp)
//                    .background(MaterialTheme.colorScheme.surface)
//            ) {
//                DrawerContent()
//            }
//        }
//    ) {
//        Scaffold(
//            topBar = { MyTopAppBar(scope, drawerState) },
//            bottomBar = { BottomNavBar(navController) }
//        ) { innerPadding ->
//            Box(modifier = Modifier.padding(innerPadding)) {
//                NavHost(navController = navController, startDestination = "home") {
//                    composable("home") { MovieListScreen(navController) }
//                    composable("favorites") { FavoritesScreen(navController) }
//                    composable("search") { SearchScreen(navController) }
//                    composable("profile") { ProfileScreen(navController) }
//                }
//            }
//        }
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyTopAppBar(scope: CoroutineScope, drawerState: DrawerState) {
//    TopAppBar(
//        title = { Text(text = "MOVIEMUSE") },
//        navigationIcon = {
//            IconButton(onClick = {
//                scope.launch {
//                    if (drawerState.isOpen) drawerState.close() else drawerState.open()
//                }
//            }) {
//                Icon(Icons.Default.Menu, contentDescription = "Open drawer")
//            }
//        },
//    )
//}
//
//@Composable
//fun DrawerContent() {
//    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
//        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
//        Spacer(modifier = Modifier.height(24.dp))
//    }
//}
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun BottomNavBar(navController: NavHostController) {
//    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
//    NavigationBar {
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
//            label = { Text("Home") },
//            selected = currentRoute == "home",
//            onClick = {
//                navController.navigate("home") {
//                    launchSingleTop = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
//            }
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Favorite, contentDescription = "Favorites") },
//            label = { Text("Favorites") },
//            selected = currentRoute == "favorites",
//            onClick = {
//                navController.navigate("favorites") {
//                    launchSingleTop = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
//            }
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
//            label = { Text("Search") },
//            selected = currentRoute == "search",
//            onClick = {
//                navController.navigate("search") {
//                    launchSingleTop = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
//            }
//        )
//        NavigationBarItem(
//            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
//            label = { Text("Profile") },
//            selected = currentRoute == "profile",
//            onClick = {
//                navController.navigate("profile") {
//                    launchSingleTop = true
//                    popUpTo(navController.graph.startDestinationId) { saveState = true }
//                }
//            }
//        )
//    }
//}
//@Composable
//fun DrawerContent(
//    isLightTheme: Boolean,
//    onThemeToggle: () -> Unit,
//    isThaiLanguage: Boolean,
//    onLanguageToggle: () -> Unit
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(text = "Settings", style = MaterialTheme.typography.headlineMedium)
//
//        Spacer(modifier = Modifier.height(24.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth().clickable { onThemeToggle() }.padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(text = "Dark Mode", modifier = Modifier.weight(1f))
//            Switch(checked = isLightTheme, onCheckedChange = { onThemeToggle() })
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        Row(
//            modifier = Modifier.fillMaxWidth().clickable { onLanguageToggle() }.padding(8.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            Text(text = "ภาษาไทย", modifier = Modifier.weight(1f))
//            Switch(checked = isThaiLanguage, onCheckedChange = { onLanguageToggle() })
//        }
//    }
//}



//package com.example.moviemuse
//
//import android.annotation.SuppressLint
//import android.app.Activity
////import android.app.LocaleManager
//import android.content.Context
//import android.os.Bundle
////import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.background
//import androidx.compose.foundation.clickable
//import androidx.compose.foundation.layout.Box
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxHeight
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.material3.*
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Favorite
//import androidx.compose.material.icons.filled.Home
//import androidx.compose.material.icons.filled.Menu
//import androidx.compose.material.icons.filled.Search
//import androidx.compose.material.icons.filled.Person
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalConfiguration
//import androidx.compose.ui.unit.dp
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.example.moviemuse.ui.theme.MovieMuseTheme
//import com.example.moviemuse.screens.LoginScreen
//import com.example.moviemuse.screens.RegisterScreen
//import com.example.moviemuse.screens.ProfileScreen
//import com.example.moviemuse.screens.ReviewScreen
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.launch
//import screens.MovieDetailScreen
//import screens.MovieListScreen
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.res.stringResource
//import screens.FavoritesScreen
//import screens.SearchScreen
//import android.widget.Toast
////import androidx.compose.ui.platform.LocalContext
//import com.example.moviemuse.utils.authenticateUser
////import androidx.compose.ui.platform.LocalContext
////import com.example.moviemuse.utils.authenticateUser
//import android.content.ContextWrapper
//import androidx.fragment.app.FragmentActivity
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.ui.input.nestedscroll.nestedScroll
//import com.example.moviemuse.model.Movie
//import com.example.moviemuse.ui.components.MovieCard
//import android.content.res.Configuration
//
//
//class MainActivity : FragmentActivity() {
//    override fun attachBaseContext(newBase: Context?) {
//
//        val context = newBase?.let { com.example.moviemuse.LocaleManager.setLocale(it) }
//        super.attachBaseContext(context)
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        val savedLanguage = com.example.moviemuse.LocaleManager.getSavedLanguage(this)
//        val isDefaultThai = savedLanguage == "th"
//
//        setContent {
//            val context = LocalContext.current
//
//            var isLightTheme by rememberSaveable { mutableStateOf(false) }
//            var isThaiLanguage by rememberSaveable { mutableStateOf(isDefaultThai) }
//
//            MovieMuseTheme(isLightTheme = isLightTheme) {
//                val navController = rememberNavController()
//                MainScreen(
//                    navController = navController,
//                    isLightTheme = isLightTheme,
//                    onThemeToggle = { isLightTheme = !isLightTheme },
//                    isThaiLanguage = isThaiLanguage,
//                    onLanguageToggle = {
//                        val newLanguageCode = if (isThaiLanguage) "en" else "th"
//                        com.example.moviemuse.LocaleManager.saveLanguage(context, newLanguageCode)
//                        isThaiLanguage = !isThaiLanguage
//                        (context as? Activity)?.recreate()
//                    }
//                )
//            }
//        }
//    }
//}
//
//@Composable
//fun MyTopAppBar(scope: CoroutineScope, drawerState: DrawerState) {
//
//}
//
//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//fun MainScreen(
//    navController: NavHostController,
//    isLightTheme: Boolean,
//    onThemeToggle: () -> Unit,
//    isThaiLanguage: Boolean,
//    onLanguageToggle: () -> Unit
//) {
//    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
//    val scope = rememberCoroutineScope()
//    val context = LocalContext.current
//    val configuration = LocalConfiguration.current
//
//    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
//    }
//ModalNavigationDrawer(
//drawerState = drawerState,
//drawerContent = { /* ✅ Drawer แสดงได้ทุก Orientation */ }
//) {
//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .clickable { scope.launch { drawerState.close() } } // ✅ ปิด Drawer ได้
//    ) {
//        Scaffold(
//            topBar = { MyTopAppBar(scope, drawerState) },
//            bottomBar = { BottomNavBar(navController, context) }
//        ) { innerPadding ->
//            Box(modifier = Modifier.padding(innerPadding)) {
//                NavHost(navController = navController, startDestination = "home") {
//                    composable("home") { MovieListScreen(navController) }
//                    composable("favorites") { FavoritesScreen(navController) }
//                    composable("search") { SearchScreen(navController) }
//                    composable("profile") { ProfileScreen(navController) }
//                }
//            }
//        }
//    }
//}
//
//
//
////    ModalNavigationDrawer(
////        drawerState = drawerState,
////        drawerContent = {
////            if (!isLandscape) {
////                Box(
////                    modifier = Modifier
////                        .fillMaxHeight()
////                        .width((configuration.screenWidthDp.dp * 0.7f))
////                        .background(MaterialTheme.colorScheme.surface)
////                ) {
////                    DrawerContent(
////                        isLightTheme = isLightTheme,
////                        onThemeToggle = {
////                            onThemeToggle()
////                            scope.launch { drawerState.close() }
////                        },
////                        isThaiLanguage = isThaiLanguage,
////                        onLanguageToggle = {
////                            onLanguageToggle()
////                            scope.launch { drawerState.close() }
////                        }
////                    )
////                }
////            }
////        }
////    ) {
////        Scaffold(
////            topBar = {
////                if (navController.currentBackStackEntryAsState().value?.destination?.route !in listOf("login", "register")) {
////                    MyTopAppBar(scope, drawerState)
////                }
////            },
////            bottomBar = {
////                if (navController.currentBackStackEntryAsState().value?.destination?.route in listOf("home", "favorites", "search", "profile")) {
////                    BottomNavBar(navController, context)
////                }
////            }
////        ) { innerPadding ->
////            Box(
////                modifier = Modifier
////                    .fillMaxSize()
////                    .padding(innerPadding)
////                    .clickable {
////                        scope.launch { drawerState.close() }
////                    }
////            ) {
////                NavHost(
////                    navController = navController,
////                    startDestination = "login",
////                ) {
////                    composable("login") { LoginScreen(navController) }
////                    composable("register") { RegisterScreen(navController) }
////                    composable("home") { MovieListScreen(navController) }
////                    composable("favorites") { FavoritesScreen(navController) }
////                    composable("search") { SearchScreen(navController) }
////                    composable("profile") { ProfileScreen(navController) }
////                    composable(
////                        "movieDetail/{movieId}",
////                        arguments = listOf(navArgument("movieId") { type = NavType.IntType })
////                    ) { backStackEntry ->
////                        val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
////                        MovieDetailScreen(movieId, navController)
////                    }
////                    composable(
////                        "movieReviews/{movieId}",
////                        arguments = listOf(navArgument("movieId") { type = NavType.IntType })
////                    ) { backStackEntry ->
////                        val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
////                        ReviewScreen(movieId = movieId, navController = navController)
////                    }
////                }
////            }
////        }
////    }
////}
//
////@Composable
////fun MainScreen(
////    navController: NavHostController,
////    isLightTheme: Boolean,
////    onThemeToggle: () -> Unit,
////    isThaiLanguage: Boolean,
////    onLanguageToggle: () -> Unit
////) {
////    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
////    val scope = rememberCoroutineScope()
////    val context = LocalContext.current
////
////    val configuration = LocalConfiguration.current
////    val halfScreenWidth = (configuration.screenWidthDp.dp * 0.7f)
////
////    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
////
////    ModalNavigationDrawer(
////        drawerState = drawerState,
////        drawerContent = {
////            Box(
////                modifier = Modifier
////                    .fillMaxHeight()
////                    .width(halfScreenWidth)
////                    .background(MaterialTheme.colorScheme.surface)
////            ) {
////                DrawerContent(
////                    isLightTheme = isLightTheme,
////                    onThemeToggle = onThemeToggle,
////                    isThaiLanguage = isThaiLanguage,
////                    onLanguageToggle = onLanguageToggle,
////                )
////            }
////        }
////    ) {
////        Scaffold(
////            topBar = {
////                if (currentRoute != "login" && currentRoute != "register") {
////                    MyTopAppBar(scope, drawerState)
////                }
////            },
////            bottomBar = {
////                if (currentRoute in listOf("home", "favorites", "search", "profile")) {
////                    BottomNavBar(navController, context)
////                }
////            }
////        ) { innerPadding ->
////            NavHost(
////                navController = navController,
////                startDestination = "login",
////                modifier = Modifier.padding(innerPadding)
////            ) {
////                composable("login") { LoginScreen(navController) }
////                composable("register") { RegisterScreen(navController) }
////                composable("home") { MovieListScreen(navController) }
////                composable("favorites") { FavoritesScreen(navController) }
////                composable("search") { SearchScreen(navController) }
////                composable("profile") { ProfileScreen(navController) }
////                composable(
////                    "movieDetail/{movieId}",
////                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
////                ) { backStackEntry ->
////                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
////                    MovieDetailScreen(movieId, navController)
////                }
////                composable(
////                    "movieReviews/{movieId}",
////                    arguments = listOf(navArgument("movieId") { type = NavType.IntType })
////                ) { backStackEntry ->
////                    val movieId = backStackEntry.arguments?.getInt("movieId") ?: 0
////                    ReviewScreen(movieId = movieId, navController = navController)
////                }
////            }
////        }
////    }
////}
//
////@OptIn(ExperimentalMaterial3Api::class)
////@Composable
////fun MyTopAppBar(scope:  CoroutineScope, drawerState: DrawerState) {
////    TopAppBar(
////        title = { Text(text = "MOVIEMUSE") },
////        navigationIcon = {
////            IconButton(onClick = {
////                scope.launch { drawerState.open() }
////            }) {
////                Icon(Icons.Default.Menu, contentDescription = "Open drawer")
////            }
////        },
////    )
////}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MyTopAppBar(scope: CoroutineScope, drawerState: DrawerState) {
//    TopAppBar(
//        title = { Text(text = "MOVIEMUSE") },
//        navigationIcon = {
//            IconButton(onClick = {
//                scope.launch {
//                    drawerState.open()
//                }
//            }) {
//                Icon(Icons.Default.Menu, contentDescription = "Open drawer")
//            }
//
//        },
//    )
//}
//
//
//@Composable
//fun DrawerContent(
//    isLightTheme: Boolean,
//    onThemeToggle: () -> Unit,
//    isThaiLanguage: Boolean,
//    onLanguageToggle: () -> Unit,
//) {
//    Column(
//        modifier = Modifier
//            .fillMaxSize()
//            .padding(16.dp)
//    ) {
//        Text(
//            text = stringResource(id = R.string.settings),
//            style = MaterialTheme.typography.headlineMedium,
//            color = MaterialTheme.colorScheme.onBackground
//        )
//        Spacer(modifier = Modifier.height(24.dp))
//
//        // Night Mode Toggle
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { onThemeToggle() }
//                .padding(vertical = 8.dp)
//        ) {
//            Text(
//                text = stringResource(id = R.string.light_mode),
//                modifier = Modifier.weight(1f),
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            Switch(
//                checked = isLightTheme,
//                onCheckedChange = { onThemeToggle() },
//                colors = SwitchDefaults.colors(
//                    checkedThumbColor = MaterialTheme.colorScheme.primary,
//                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
//                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
//                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            )
//        }
//
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // Thai Language Toggle
//        Row(
//            verticalAlignment = Alignment.CenterVertically,
//            modifier = Modifier
//                .fillMaxWidth()
//                .clickable { onLanguageToggle() }
//                .padding(vertical = 8.dp)
//        ) {
//            Text(
//                text = stringResource(id = R.string.language),
//                modifier = Modifier.weight(1f),
//                color = MaterialTheme.colorScheme.onBackground
//            )
//            Switch(
//                checked = isThaiLanguage,
//                onCheckedChange = { onLanguageToggle() },
//                colors = SwitchDefaults.colors(
//                    checkedThumbColor = MaterialTheme.colorScheme.primary,
//                    checkedTrackColor = MaterialTheme.colorScheme.primaryContainer,
//                    uncheckedThumbColor = MaterialTheme.colorScheme.onSurface,
//                    uncheckedTrackColor = MaterialTheme.colorScheme.surfaceVariant
//                )
//            )
//        }
//    }
//}
//
//fun getFragmentActivity(context: Context): FragmentActivity? {
//    var ctx = context
//    while (ctx is ContextWrapper) {
//        if (ctx is FragmentActivity) return ctx  // ✅ Ensure we get a FragmentActivity
//        ctx = ctx.baseContext
//    }
//    return null
//}
//
//
//@Composable
//fun BottomNavBar(navController: NavHostController, context: Context) {
//    val navBackStackEntry by navController.currentBackStackEntryAsState()
//    val currentRoute = navBackStackEntry?.destination?.route
//    val context = LocalContext.current  // ✅ Retrieve context inside a Composable
//    val activity = getFragmentActivity(context)
//
//
//    NavigationBar {
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    imageVector = Icons.Default.Home,
//                    contentDescription = stringResource(id = R.string.home)
//                )
//            },
//            label = { Text(stringResource(id = R.string.home)) },
//            selected = currentRoute == "home",
//            onClick = {
//                navController.navigate("home") {
//                    launchSingleTop = true
//                    popUpTo("home") { saveState = true }
//                }
//            }
//        )
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    imageVector = Icons.Default.Favorite,
//                    contentDescription = stringResource(id = R.string.favorites)
//                )
//            },
//            label = { Text(stringResource(id = R.string.favorites)) },
//            selected = currentRoute == "favorites",
//            onClick = {
//                navController.navigate("favorites") {
//                    launchSingleTop = true
//                    popUpTo("home") { saveState = true }
//                }
//            }
//        )
//        NavigationBarItem(
//            icon = {
//                Icon(
//                    imageVector = Icons.Default.Search,
//                    contentDescription = stringResource(id = R.string.search)
//                )
//            },
//            label = { Text(stringResource(id = R.string.search)) },
//            selected = currentRoute == "search",
//            onClick = {
//                navController.navigate("search") {
//                    launchSingleTop = true
//                    popUpTo("home") { saveState = true }
//                }
//            }
//        )
//        NavigationBarItem(
//            icon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Profile") },
//            label = { Text("Profile") },
//            selected = navController.currentBackStackEntryAsState().value?.destination?.route == "profile",
//            onClick = {
//                if (activity != null) {
//                    authenticateUser(
//                        context = activity,
//                        onAuthenticated = {
//                            navController.navigate("profile") {
//                                launchSingleTop = true
//                                popUpTo("home") { saveState = true }
//                            }
//                        },
//                        onFailed = {
//                            Toast.makeText(activity, "Biometric authentication failed", Toast.LENGTH_SHORT).show()
//                        }
//                    )
//                } else {
//                    Toast.makeText(context, "Error: Unable to get activity context", Toast.LENGTH_SHORT).show()
//                }
//            }
//        )
//
//
//    }
//}
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun MovieScreenWithCollapsingToolbar(movies: List<Movie>, onMovieClick: (Movie) -> Unit) {
//    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
//
//    Scaffold(
//        topBar = {
//            TopAppBar(title = { Text("Movie Muse") }, scrollBehavior = scrollBehavior)
//        }
//    ) { paddingValues ->
//        LazyColumn(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(paddingValues)
//                .nestedScroll(scrollBehavior.nestedScrollConnection),
//            verticalArrangement = Arrangement.spacedBy(8.dp)
//        ) {
//            items(movies) { movie ->
//                MovieCard(movie = movie, onClick = { onMovieClick(movie) })
//            }
//        }
//    }
//}
//
