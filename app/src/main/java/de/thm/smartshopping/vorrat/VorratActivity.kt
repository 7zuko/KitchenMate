package de.thm.smartshopping.vorrat

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import de.thm.smartshopping.DashboardTopAppBar
import de.thm.smartshopping.ui.theme.SmartShoppingTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VorratScreen(
	toggleSignal: Int
) {
	val listState = rememberLazyListState()

	var showActionsPanelFully by rememberSaveable { mutableStateOf(true) }

	val actionsPanelMaxHeightDp = 100.dp
	val density = LocalDensity.current

	val actionsPanelMaxHeightPx = rememberSaveable(density, actionsPanelMaxHeightDp) {
		with(density) { actionsPanelMaxHeightDp.toPx() }
	}
	val snapThresholdPx = actionsPanelMaxHeightPx * 0.6f

	var currentPanelVisibleHeightPx by rememberSaveable { mutableFloatStateOf(0f) }
	val coroutineScope = rememberCoroutineScope()

	//Trigger von außen (Filter Button)
	var oldToggleSignal by rememberSaveable { mutableIntStateOf(toggleSignal) }
	LaunchedEffect(toggleSignal) {
		println("$toggleSignal , $oldToggleSignal")
		if (toggleSignal != oldToggleSignal) {
			coroutineScope.launch { showActionsPanelFully = !showActionsPanelFully }
			oldToggleSignal = toggleSignal
		}
	}

	LaunchedEffect(showActionsPanelFully, actionsPanelMaxHeightPx) {
		if (showActionsPanelFully) {
			currentPanelVisibleHeightPx = actionsPanelMaxHeightPx
		} else {
			currentPanelVisibleHeightPx = 0f
		}
	}

	val nestedScrollConnection = remember {
		object : NestedScrollConnection {
			override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
				val isAtTop = listState.firstVisibleItemIndex == 0 && listState.firstVisibleItemScrollOffset == 0

				//Condition 1: Revealing the panel by pulling down at the top
				if (isAtTop && available.y > 0 && source == NestedScrollSource.Drag && !showActionsPanelFully) {
					val newHeight = (currentPanelVisibleHeightPx + available.y * 0.6f)
						.coerceIn(0f, actionsPanelMaxHeightPx * 1.3f)
					val consumedY = newHeight - currentPanelVisibleHeightPx
					currentPanelVisibleHeightPx = newHeight
					return Offset(0f, consumedY / 0.6f)
				}
				//Condition 2: Hiding the panel by pushing it up  (e.g. when its partially revealed)
				if (currentPanelVisibleHeightPx > 0 && available.y < 0 && source == NestedScrollSource.Drag && !showActionsPanelFully) {
					val newHeight = (currentPanelVisibleHeightPx + available.y)
						.coerceIn(0f, actionsPanelMaxHeightPx)
					val consumedY = newHeight - currentPanelVisibleHeightPx
					currentPanelVisibleHeightPx = newHeight
					return Offset(0f, consumedY)
				}

				//Condition 3: Parallax Hide - Hiding the panel when scrolling list content down
				if (currentPanelVisibleHeightPx > 0 && available.y < 0 && source == NestedScrollSource.Drag) {
					val parallaxFactor = 0.5f // resistance when scrolling down
					val deltaToConsume = (available.y * parallaxFactor)
						.coerceAtLeast(-currentPanelVisibleHeightPx)

					currentPanelVisibleHeightPx += deltaToConsume
					currentPanelVisibleHeightPx = currentPanelVisibleHeightPx.coerceIn(0f, actionsPanelMaxHeightPx)

					if (currentPanelVisibleHeightPx == 0f) {
						if (showActionsPanelFully) {
							coroutineScope.launch { showActionsPanelFully = false }
						}
					} else if (currentPanelVisibleHeightPx < actionsPanelMaxHeightPx && showActionsPanelFully) {
						// If we are dragging it closed and it was "fully open",
						// we might want to set showActionsPanelFully to false earlier
						// so that if the drag stops, the fling/release logic doesn't try to reopen it.
						// This depends on desired snap behavior.
						// For now, let's allow `showActionsPanelFully` to remain true until it's fully closed by drag
						// or by the snap logic later.
					}
					return Offset(0f, deltaToConsume / parallaxFactor)
				}

				return Offset.Zero
			}

			override suspend fun onPostFling(consumed: Velocity, available: Velocity): Velocity {
				val targetHeightBasedOnFullState = if (showActionsPanelFully) actionsPanelMaxHeightPx else 0f
				if (currentPanelVisibleHeightPx != targetHeightBasedOnFullState ||
					currentPanelVisibleHeightPx > 0 && currentPanelVisibleHeightPx < actionsPanelMaxHeightPx) {

					if (currentPanelVisibleHeightPx > snapThresholdPx) {
						coroutineScope.launch { showActionsPanelFully = true }
					} else {
						if (showActionsPanelFully || currentPanelVisibleHeightPx > 0) {
							coroutineScope.launch { showActionsPanelFully = false }
						}
					}
				}
				return super.onPostFling(consumed, available)
			}

			override fun onPostScroll(consumed: Offset, available: Offset, source: NestedScrollSource): Offset {
				if (source == NestedScrollSource.Drag &&
					currentPanelVisibleHeightPx > 0f && currentPanelVisibleHeightPx < actionsPanelMaxHeightPx) {
					if (currentPanelVisibleHeightPx > snapThresholdPx) {
						coroutineScope.launch { showActionsPanelFully = true }
					} else {
						coroutineScope.launch { showActionsPanelFully = false }
					}
				}
				return Offset.Zero
			}
		}
	}

	val animatedPanelHeightDp by animateDpAsState(
		targetValue = with(density) { currentPanelVisibleHeightPx.toDp() },
		animationSpec = spring(
			dampingRatio = Spring.DampingRatioNoBouncy,
			stiffness = Spring.StiffnessMediumLow
		),
		label = "ActionsPanelHeightAnimation"
	)

	Scaffold(
		topBar = {
			DashboardTopAppBar(
				title = {
					Text("Vorrat")
				},
				showNavigationIcon = false,
				actions = {
					IconButton(onClick = { println("Search clicked") }) {
						Icon(Icons.Filled.Search, contentDescription = "Search")
					}
					/*
					IconButton(onClick = { showMenu = !showMenu }) {
						Icon(Icons.Filled.MoreVert, contentDescription = "More options")
					}
					DropdownMenu(
						expanded = showMenu,
						onDismissRequest = { showMenu = false }
					) {
						DropdownMenuItem(
							leadingIcon = { Icon(Icons.Filled.Edit, contentDescription = "Verwalten") },
							text = { Text("Verwalten") },
							onClick = { showMenu = false }
						)
						DropdownMenuItem(
							leadingIcon = { Icon(Icons.Filled.Settings, contentDescription = "Einstellungen") },
							text = { Text("Einstellungen") },
							onClick = { showMenu = false }
						)
					}
					 */
				}
			)
		}
	) { paddingValues ->
		Column(
			modifier = Modifier
				.padding(paddingValues)
				.fillMaxSize()
				.nestedScroll(nestedScrollConnection)
		) {
			// Filter Panel
			Box(
				modifier = Modifier
					.fillMaxWidth()
					.height(animatedPanelHeightDp)
					.background(MaterialTheme.colorScheme.surfaceVariant)
					.clipToBounds()
			) {
				//if (animatedPanelHeightDp > 1.dp) {
				FilterDropDown()
				//}
			}
			if (animatedPanelHeightDp > 1.dp) {
				HorizontalDivider()
			}
			// List
			LazyColumn(
				state = listState,
				modifier = Modifier
					.fillMaxSize()
					.wrapContentSize()
			) {
				items(50) { index ->
					Text("Item ${index + 1}", modifier = Modifier
						.padding(16.dp)
						.fillMaxWidth())
					if (index < 49) HorizontalDivider()
				}
			}
		}
	}
}

@Composable
fun FilterDropDown() {
	val scrollState = rememberScrollState()

	Row(
		modifier = Modifier
			.fillMaxSize()
			.horizontalScroll(scrollState),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.Start
	) {
		FilterButton(
			text = "Alles",
			toggled = true
		)
		VerticalDividerFilterDropDown()
		FilterButton(
			text = "Gewürze",
			toggled = false
		)
		VerticalDividerFilterDropDown()
		FilterButton(
			text = "Obst & Gemüse",
			toggled = false
		)
		VerticalDividerFilterDropDown()
		FilterButton(
			text = "Backwaren",
			toggled = false
		)
		VerticalDividerFilterDropDown()
		FilterButton(
			text = "Getränke",
			toggled = false
		)
		VerticalDividerFilterDropDown()
		FilterButton(
			text = "Süßigkeiten",
			toggled = false
		)
		VerticalDividerFilterDropDown()
		FilterButton(
			text = "Sonstige",
			icon = Icons.Outlined.ShoppingCart,
			toggled = false
		)
	}
}

@Composable
fun FilterButton(
	text: String,
	modifier: Modifier = Modifier,
	icon: ImageVector? = null,
	toggled: Boolean = false,
) {
	var isToggled by rememberSaveable { mutableStateOf(toggled) }

	Button(
		onClick = {
			isToggled = !isToggled
			//TODO: Add action
		},
		shape = RectangleShape,
		colors = ButtonDefaults.buttonColors(
			containerColor = if (isToggled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.background,
			contentColor = if (isToggled) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onBackground
		),
		modifier = modifier
			.fillMaxHeight()
			.width(130.dp)
	) {
		if (icon != null) {
			Column(
				horizontalAlignment = Alignment.CenterHorizontally
			) {
				Icon(
					modifier = Modifier
						.weight(1.3f)
						.fillMaxSize(),
					imageVector = icon,
					contentDescription = "$text anzeigen",
				)
				Text(
					modifier = Modifier.weight(0.8f),
					textAlign = TextAlign.Center,
					text = text
				)
			}
		} else {
			Text(
				modifier = Modifier.weight(0.8f),
				textAlign = TextAlign.Center,
				text = text
			)
		}
	}
}

@Composable
fun VerticalDividerFilterDropDown() {
	VerticalDivider(
		modifier = Modifier
			.fillMaxHeight(),
		thickness = 1.dp,
		color = DividerDefaults.color
	)
}

@Preview(showBackground = true)
@Composable
fun FilterDropDownPreview() {
	SmartShoppingTheme {
		Box(
			modifier = Modifier
				.fillMaxWidth()
				.height(100.dp)
				.background(MaterialTheme.colorScheme.surfaceVariant)
				.clipToBounds()
		) {
			FilterDropDown()
		}
	}
}

@Preview(showBackground = true)
@Composable
fun VorratScreenPreview() {
	SmartShoppingTheme {
		VorratScreen(
			toggleSignal = 0
		)
	}
}