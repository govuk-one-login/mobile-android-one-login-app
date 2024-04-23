package uk.gov.onelogin.ui.main

import android.annotation.SuppressLint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import uk.gov.ui.components.navigation.GdsNavigationBar
import uk.gov.ui.components.navigation.GdsNavigationItem

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MainNavScreen(
    navController: NavHostController = rememberNavController()
) {
    val navItems = listOf(
        BottomNavDestination.Home,
        BottomNavDestination.Wallet,
        BottomNavDestination.Profile
    )
    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            GdsNavigationBar(
                items = navItems.map { navDest ->
                    GdsNavigationItem(
                        icon = {
                            Icon(painterResource(id = navDest.icon), navDest.key)
                        },
                        onClick = {
                            navController.navigate(navDest.key) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        selected = navBackStackEntry?.destination?.route == navDest.key,
                        label = {
                            Text(
                                text = stringResource(id = navDest.label),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                fontSize = MaterialTheme.typography.bodyLarge.fontSize
                            )
                        }
                    )
                },
                tonalElevation = 0.dp
            ).generate()
        }
    ) { }
}
