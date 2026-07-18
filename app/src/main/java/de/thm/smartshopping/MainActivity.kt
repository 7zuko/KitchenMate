package de.thm.smartshopping

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.automirrored.outlined.MenuBook
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import de.thm.smartshopping.methods.enterTransitionDuration
import de.thm.smartshopping.methods.navBarHeight
import de.thm.smartshopping.ui.destinations.artikelverwaltung.ArtikelVerwaltungScreen
import de.thm.smartshopping.ui.destinations.artikelverwaltung.viewmodels.ArtikelVerwaltungViewModel
import de.thm.smartshopping.ui.destinations.einkaufslisten.EinkaufslistenAnsicht
import de.thm.smartshopping.ui.destinations.einkaufslisten.EinkaufslistenScreen
import de.thm.smartshopping.ui.destinations.einkaufslisten.EinkaufslistenShoppingModeScreen
import de.thm.smartshopping.ui.destinations.einkaufslisten.viewmodels.EinkaufslistenAnsichtViewModel
import de.thm.smartshopping.ui.destinations.einkaufslisten.viewmodels.EinkaufslistenViewModel
import de.thm.smartshopping.ui.destinations.rezepte.RezepteScreen
import de.thm.smartshopping.ui.destinations.rezepte.details.RezeptDetailScreen
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import de.thm.smartshopping.ui.destinations.rezepte.viewmodels.RezepteViewModel
import androidx.compose.runtime.remember
import de.thm.smartshopping.ui.destinations.dashboard.DashboardScreen
import de.thm.smartshopping.ui.destinations.speiseplan.SpeiseplanScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
	@RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		enableEdgeToEdge()
		setContent {
			SmartShoppingTheme {
				MainScreen()
			}
		}
	}
}

sealed class NavDestination(
	val title: String,
	val route: String,
	val selectedIcon: ImageVector,
	val unselectedIcon: ImageVector,
) {
	object Dashboard : NavDestination(
		title = "Start",
		route = "dashboard",
		selectedIcon = Icons.Default.Home,
		unselectedIcon = Icons.Outlined.Home
	)
	object Verwaltung : NavDestination(
		title = "Verwaltung",
		route = "artikelverwaltung",
		selectedIcon = Icons.AutoMirrored.Filled.List,
		unselectedIcon = Icons.AutoMirrored.Outlined.List
	)

	object Rezepte : NavDestination(
		title = "Rezepte",
		route = "rezepte",
		selectedIcon = Icons.AutoMirrored.Filled.MenuBook,
		unselectedIcon = Icons.AutoMirrored.Outlined.MenuBook
	)

	object Speiseplan : NavDestination(
		title = "Speiseplan",
		route = "speiseplan",
		selectedIcon = Icons.Filled.CalendarMonth,
		unselectedIcon = Icons.Outlined.CalendarMonth
	)

	object Einkaufslisten : NavDestination(
		title = "Einkaufen",
		route = "einkaufslisten",
		selectedIcon = Icons.Filled.ShoppingCart,
		unselectedIcon = Icons.Outlined.ShoppingCart
	)

	companion object {
		fun fromRoute(route: String?): NavDestination? {
			return when (route) {
				Dashboard.route -> Dashboard
				Verwaltung.route -> Verwaltung
				Rezepte.route -> Rezepte
				Speiseplan.route -> Speiseplan
				Einkaufslisten.route -> Einkaufslisten
				else -> null
			}
		}
	}
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun MainScreen(
	viewModel: DashboardViewModel = hiltViewModel(),
) {
	//NavBar
	val items = listOf(
		NavDestination.Dashboard,
		NavDestination.Verwaltung,
		NavDestination.Rezepte,
		NavDestination.Speiseplan,
		NavDestination.Einkaufslisten
	)

	//NavController
	val navController = rememberNavController()

	val startRoute = NavDestination.Dashboard

	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentRoute: String? = navBackStackEntry?.destination?.route

	var tempRoute by remember { mutableStateOf<String?>(null) }
	var previousRoute by remember { mutableStateOf<String?>(null) }
	LaunchedEffect(currentRoute) {
		if (tempRoute != currentRoute) {
			previousRoute = tempRoute
		}
		tempRoute = currentRoute
	}

	//NavBar animation logic
	var oldItemIndex by remember { mutableIntStateOf(0) }
	var selectedItemIndex by remember { mutableIntStateOf(0) }

	val uiState by viewModel.initialCheckUiState.collectAsState()
	var navigationAttempted by remember { mutableStateOf(false) }

	LaunchedEffect(uiState) {
		if (!uiState.isLoading && !navigationAttempted) {
			uiState.navigateToShoppingListId?.let { einkaufslisteId ->
				navController.navigate("einkaufslisten_shoppingmode/$einkaufslisteId") {
					popUpTo(startRoute.route) { inclusive = true }
				}
				navigationAttempted = true
			}
			navigationAttempted = true
		}
	}

	if (uiState.isLoading) {
		Column(
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			CircularProgressIndicator()

			Spacer(Modifier.height(16.dp))

			Text(
				"Kitchen Mate lädt...",
				style = MaterialTheme.typography.bodyLarge
			)
		}
	} else {
		Scaffold(
			bottomBar = {
				if (currentRoute != "einkaufslisten_shoppingmode/{einkaufslisteId}") {
					NavigationBar(

						containerColor = MaterialTheme.colorScheme.surface,

						tonalElevation = 8.dp

					) {
						items.forEachIndexed { index, screen ->
							NavigationBarItem(
								icon = {
									Icon(
										imageVector =
											if (currentRoute == screen.route)
												screen.selectedIcon
											else
												screen.unselectedIcon,
										contentDescription = screen.title
									)
								},
								label = { Text(screen.title) },
								selected = currentRoute == screen.route,
								onClick = {
									oldItemIndex = selectedItemIndex
									selectedItemIndex = index

									navController.navigate(screen.route) {
										popUpTo(navController.graph.findStartDestination().id) {
											saveState = true
										}
										launchSingleTop = true
										restoreState = true
									}
								},
								alwaysShowLabel = false
							)
						}
					}
				}
			}
		) { scaffoldPadding ->
			val layoutDirection = LocalLayoutDirection.current
			NavHost(
				navController = navController,
				startDestination = startRoute.route,
				modifier = Modifier.padding(end = scaffoldPadding.calculateEndPadding(layoutDirection)),
				enterTransition = {
					if (oldItemIndex < selectedItemIndex
						&& !currentRoute.toString().startsWith(previousRoute.toString().split("_").first())) {
						slideInHorizontally(tween(enterTransitionDuration)) { fullWidth -> fullWidth } + fadeIn(tween(enterTransitionDuration / 2))
					} else if (!currentRoute.toString().startsWith(previousRoute.toString().split("_").first())) {
						slideInHorizontally(tween(enterTransitionDuration)) { fullWidth -> -fullWidth } + fadeIn(tween(enterTransitionDuration / 2))
					} else {
						//subnavigation
						fadeIn(tween(700))
					}
				},
				exitTransition = {
					if (oldItemIndex < selectedItemIndex
						&& !currentRoute.toString().startsWith(previousRoute.toString().split("_").first())) {
						slideOutHorizontally(tween(enterTransitionDuration)) { fullWidth -> -fullWidth }
					} else if (!currentRoute.toString().startsWith(previousRoute.toString().split("_").first())) {
						slideOutHorizontally(tween(enterTransitionDuration)) { fullWidth -> fullWidth }
					} else {
						//subnavigation
						fadeOut(tween(700))
					}
				}
			) {
				composable(route = NavDestination.Dashboard.route) {
					DashboardScreen(
						navController = navController
					)
				}

				composable(route = NavDestination.Verwaltung.route) {
					val viewModel: ArtikelVerwaltungViewModel = hiltViewModel()
					val state by viewModel.state.collectAsState()
					ArtikelVerwaltungScreen(
						state = state,
						onEvent = viewModel::onEvent,
						navController = navController
					)
				}
				composable(route = NavDestination.Rezepte.route) {

					val viewModel: RezepteViewModel = hiltViewModel()

					val state by viewModel.state.collectAsState()

					RezepteScreen(
						state = state,
						onEvent = viewModel::onEvent,
						navController = navController
					)
				}
				composable(
					route = "rezept_details/{rezeptId}",
					arguments = listOf(
						navArgument("rezeptId") {
							type = NavType.StringType
						}
					)
				) { backStackEntry ->

					val rezeptId =
						backStackEntry.arguments?.getString("rezeptId")

					println("Route RezeptId: $rezeptId")

					val parentEntry = remember(backStackEntry) {
						navController.getBackStackEntry(
							NavDestination.Rezepte.route
						)
					}

					val viewModel: RezepteViewModel =
						hiltViewModel(parentEntry)

					val state by viewModel.state.collectAsState()

					if (rezeptId != null) {

						RezeptDetailScreen(
							rezeptId = rezeptId,
							state = state,
							onEvent = viewModel::onEvent,
							navController = navController
						)

					} else {

						TestBox(
							"Rezept nicht gefunden"
						) {
							navController.popBackStack()
						}
					}
				}

				composable(
					route = NavDestination.Speiseplan.route
				) {

					SpeiseplanScreen()

				}

				//Einkaufslisten
				composable(route = NavDestination.Einkaufslisten.route) {
					val viewModel: EinkaufslistenViewModel = hiltViewModel()
					val state by viewModel.state.collectAsState()
					EinkaufslistenScreen(state = state, onEvent = viewModel::onEvent, navController = navController)
				}
				composable(route = "einkaufslisten_neu") {
					TestBox("Einkaufsliste erstellen", { navController.popBackStack() } )
				}
				composable(
					route = "einkaufslisten_ansicht/{einkaufslisteId}",
					arguments = listOf(navArgument("einkaufslisteId") { type = NavType.StringType })
				) { navBackStackEntry ->
					val einkaufslisteId = navBackStackEntry.arguments?.getString("einkaufslisteId")

					if(einkaufslisteId != null) {
						val viewModel: EinkaufslistenAnsichtViewModel = hiltViewModel()
						val state by viewModel.state.collectAsState()
						EinkaufslistenAnsicht(id = einkaufslisteId, state = state, onEvent = viewModel::onEvent, navController = navController)
					} else {
						TestBox("Einkaufsliste nicht gefunden", { navController.popBackStack() } )
					}
				}
				composable(
					route = "einkaufslisten_shoppingmode/{einkaufslisteId}",
					arguments = listOf(navArgument("einkaufslisteId") { type = NavType.StringType })
				) { navBackStackEntry ->
					val einkaufslisteId = navBackStackEntry.arguments?.getString("einkaufslisteId")

					if(einkaufslisteId != null) {
						val viewModel: EinkaufslistenAnsichtViewModel = hiltViewModel()
						val state by viewModel.state.collectAsState()
						EinkaufslistenShoppingModeScreen(id = einkaufslisteId, state = state, onEvent = viewModel::onEvent, navController = navController)
					} else {
						TestBox("Einkaufsliste nicht gefunden", { navController.popBackStack() } )
					}
				}
			}
		}
	}
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
	SmartShoppingTheme {
		MainScreen()
	}
}

@Composable
fun TestBox(name: String, onNavigationIconClick: () -> Unit = {}) {
	Scaffold(
		modifier = Modifier.padding(bottom = navBarHeight),
		topBar = {
			DashboardTopAppBar(
				title = {
					Text(name)
				},
				showNavigationIcon = false,
				onNavigationIconClick = onNavigationIconClick
			)
		}
	) { paddingValues ->
		Box(
		modifier = Modifier
			.padding(paddingValues)
			.fillMaxSize(),
		contentAlignment = Alignment.Center
		) {
		Text(
			text = name,
			fontSize = 20.sp
		)
		}
	}
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardTopAppBar(
	title: @Composable () -> Unit,
	showNavigationIcon: Boolean = true,
	onNavigationIconClick: (() -> Unit)? = null,
	navigationIcon: @Composable () -> Unit = { // Default navigation icon
		if (showNavigationIcon) {
			IconButton(onClick = { onNavigationIconClick?.invoke() }) {
				Icon(
					imageVector = Icons.AutoMirrored.Filled.ArrowBack,
					contentDescription = "Navigation"
				)
			}
		}
	},
	actions: @Composable RowScope.() -> Unit = {},
)
{
	TopAppBar(
		title = title,
		navigationIcon = if (onNavigationIconClick != null || showNavigationIcon) navigationIcon else {
			{}
		},
		actions = actions,
		colors = TopAppBarDefaults.topAppBarColors(
			containerColor = MaterialTheme.colorScheme.surface,
			titleContentColor = MaterialTheme.colorScheme.onSurface
		)
	)
}

@Preview(showBackground = true)
@Composable
fun DashboardTopAppBarPreview() {
	SmartShoppingTheme {
		DashboardTopAppBar(
			title = {
				Text("Test")
			}
		)
	}
}