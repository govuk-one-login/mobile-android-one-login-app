package uk.gov.onelogin.mainnav.ui

import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.gov.android.ui.theme.m3.NavigationElements
import uk.gov.android.ui.theme.m3.toMappedColors
import uk.gov.onelogin.mainnav.graphs.BottomNavGraph.bottomGraph

@Suppress("LongMethod")
@Composable
fun MainNavScreen(
    navController: NavHostController = rememberNavController(),
    mainNavScreenViewModel: MainNavViewModel = hiltViewModel(),
    analyticsViewModel: MainNavAnalyticsViewModel = hiltViewModel()
) {
    val displayContentAsFullScreen = remember {
        mainNavScreenViewModel.displayContentAsFullScreenState
    }

    navController.addOnDestinationChangedListener { _, _, _ ->
        mainNavScreenViewModel.setDisplayContentAsFullScreenState(false)
    }

    val navItems = createBottomNavItems(
        mainNavScreenViewModel.walletEnabled,
        { analyticsViewModel.trackHomeTabButton() },
        { analyticsViewModel.trackWalletTabButton() },
        { analyticsViewModel.trackSettingsTabButton() }
    )
    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        mainNavScreenViewModel.checkWalletEnabled()
    }

    LaunchedEffect(mainNavScreenViewModel.walletDeepLinkReceived) {
        if (mainNavScreenViewModel.walletDeepLinkReceived.value) {
            bottomNav(
                navController,
                navItems.first { it.first == BottomNavDestination.Wallet }
            )
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0.dp),
        bottomBar = {
            if (!displayContentAsFullScreen.value) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    tonalElevation = NavigationBarDefaults.Elevation,
                    windowInsets = NavigationBarDefaults.windowInsets
                ) {
                    navItems.map { navDest ->
                        NavBarItem(
                            navigationDestination = navDest,
                            navController = navController,
                            selected = navBackStackEntry?.destination?.route == navDest.first.key
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (
                mainNavScreenViewModel.walletDeepLinkReceived.value
            ) {
                BottomNavDestination.Wallet.key
            } else {
                BottomNavDestination.Home.key
            },
            modifier = Modifier.padding(paddingValues)
        ) {
            bottomGraph { mainNavScreenViewModel.setDisplayContentAsFullScreenState(newValue = it) }
        }
    }
}

@Composable
private fun RowScope.NavBarItem(
    navigationDestination: Pair<BottomNavDestination, () -> Unit>,
    selected: Boolean,
    navController: NavHostController
) {
    NavigationBarItem(
        selected = selected,
        onClick = {
            bottomNav(
                navController,
                navigationDestination
            )
        },
        icon = {
            Icon(
                painter = painterResource(navigationDestination.first.icon),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        label = {
            Label(text = navigationDestination.first.label)
        },
        alwaysShowLabel = true,
        colors = NavigationBarItemDefaults.colors(
            indicatorColor =
            NavigationElements.navigationBarSelectedState.toMappedColors()
        )
    )
}

@Composable
private fun Label(text: Int) {
    Text(
        text = stringResource(id = text),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        fontSize = MaterialTheme
            .typography.bodyMedium.fontSize.nonScaledSp,
        fontWeight = FontWeight.Bold
    )
}

/**
 * Based on the WalletFeatureFlag, it returns a [Pair] of [BottomNavDestination] and function to
 * allow for displaying the correct tabs, but also log analytics based on what tab has been clicked.
 *
 * @param walletEnabled - Wallet feature flag value which controls if the Wallet tab is displayed
 * @param trackHome - GA4 `trackEventIcon` for Home tab button
 * @param trackWallet - - GA4 `trackEventIcon` for Wallet tab button
 * @param trackProfile - GA4 `trackEventIcon` for Profile tab button
 */
private fun createBottomNavItems(
    walletEnabled: State<Boolean>,
    trackHome: () -> Unit,
    trackWallet: () -> Unit,
    trackProfile: () -> Unit
): List<Pair<BottomNavDestination, () -> Unit>> {
    val home = BottomNavDestination.Home to trackHome
    val wallet = BottomNavDestination.Wallet to trackWallet
    val profile = BottomNavDestination.Settings to trackProfile
    return if (walletEnabled.value) {
        listOf(home, wallet, profile)
    } else {
        listOf(home, profile)
    }
}

private fun bottomNav(
    navController: NavHostController,
    navDest: Pair<BottomNavDestination, () -> Unit>
) {
    navDest.second()
    navController.navigate(navDest.first.key) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

val TextUnit.nonScaledSp
    @Composable
    get() = (this.value / LocalDensity.current.fontScale).sp
