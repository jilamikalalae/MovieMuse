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
import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.compose.material.icons.filled.Close
import androidx.activity.compose.BackHandler



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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }
    }
    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    NOTIFICATION_PERMISSION_CODE
                )
            }
        }
    }
    companion object {
        const val NOTIFICATION_PERMISSION_CODE = 100
    }
}


@Composable
fun MainScreen(
    navController: NavHostController,
    isLightTheme: Boolean,
    onThemeToggle: () -> Unit,
    isThaiLanguage: Boolean,
    onLanguageToggle: () -> Unit
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    // ✅ ใช้ `BackHandler` ให้แน่ใจว่าทำงานได้
    BackHandler(enabled = drawerState.isOpen) {
        scope.launch { drawerState.close() }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = drawerState.isOpen,
        drawerContent = {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(250.dp)
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                DrawerContent(
                    isLightTheme = isLightTheme,
                    onThemeToggle = onThemeToggle,
                    isThaiLanguage = isThaiLanguage,
                    onLanguageToggle = onLanguageToggle,
                    onCloseDrawer = { scope.launch { drawerState.close() } }
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
                    BottomNavBar(navController, LocalContext.current)
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        if (drawerState.isOpen) {
                            scope.launch { drawerState.close() }
                        }
                    }
            ) {
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
                scope.launch { drawerState.open() }
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
    onLanguageToggle: () -> Unit,
    onCloseDrawer: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        IconButton(
            onClick = onCloseDrawer,
            modifier = Modifier.align(Alignment.End)
        ) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close Drawer")
        }

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
                text = stringResource(id = R.string.light_mode),
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.onBackground
            )
            Switch(
                checked = isLightTheme,
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
                            Toast.makeText(
                                activity,
                                "Biometric authentication failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                } else {
                    Toast.makeText(
                        context,
                        "Error: Unable to get activity context",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
}