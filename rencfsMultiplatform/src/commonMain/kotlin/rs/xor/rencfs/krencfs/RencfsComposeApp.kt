package rs.xor.rencfs.krencfs

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import kotlinx.coroutines.launch
import rs.xor.rencfs.krencfs.data.sqldelight.SQLDelightDB
import rs.xor.rencfs.krencfs.ui.screens.AboutScreen
import rs.xor.rencfs.krencfs.ui.screens.SettingsScreen
import rs.xor.rencfs.krencfs.ui.screens.VaultDetailScreen
import rs.xor.rencfs.krencfs.ui.screens.VaultListScreen

enum class RencfsScreen(
    val route: String,
    val title: String,
    val icon: ImageVector,
    val showInBottomBar: Boolean = true
) {
    VaultList("vaults", "Vaults", Icons.Filled.Folder),
    VaultDetail("vault/{vaultId}", "Vault Details", Icons.Filled.Edit, false),
    Settings("settings", "Settings", Icons.Filled.Settings),
    About("about", "About", Icons.Filled.Info);

    companion object {
        fun vaultDetailRoute(vaultId: String) = "vault/$vaultId"
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RencfsComposeApp() {
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var isEditing by remember { mutableStateOf(false) }

    val currentScreen = RencfsScreen.entries.find {
        it.route == currentBackStackEntry?.destination?.route
    } ?: RencfsScreen.VaultList

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentScreen.title) },
                navigationIcon = {
                    if (currentScreen == RencfsScreen.VaultDetail) {
                        IconButton(onClick = {
                            if (isEditing) {
                                isEditing = false
                            } else {
                                navController.navigateUp()
                            }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                        }
                    }
                },
                actions = {
                    if (currentScreen == RencfsScreen.VaultDetail) {
                        if (isEditing) {
                            // Show save button when editing
                            IconButton(onClick = {
                                // Save action will be handled by the screen
                                isEditing = false
                            }) {
                                Icon(Icons.Filled.Save, "Save")
                            }
                        } else {
                            // Show edit button when viewing
                            IconButton(onClick = { isEditing = true }) {
                                Icon(Icons.Filled.Edit, "Edit")
                            }
                        }
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                RencfsScreen.entries
                    .filter { it.showInBottomBar }
                    .forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentScreen == screen,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
            }
        },
        floatingActionButton = {
            if (currentScreen == RencfsScreen.VaultList) {
                FloatingActionButton(
                    onClick = {
                        scope.launch {
                            SQLDelightDB.getVaultRepositoryAsync().addVault()
                        }
                    }
                ) {
                    Icon(Icons.Filled.Add, "Add Vault")
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = RencfsScreen.VaultList.route,
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            composable(RencfsScreen.VaultList.route) {
                VaultListScreen(
                    onVaultSelected = { vaultId ->
                        isEditing = false // Reset edit mode when navigating
                        navController.navigate(RencfsScreen.vaultDetailRoute(vaultId))
                    }
                )
            }
            composable(
                route = RencfsScreen.VaultDetail.route,
                arguments = listOf(navArgument("vaultId") { type = NavType.StringType })
            ) { backStackEntry ->
                val vaultId = backStackEntry.arguments?.getString("vaultId")
                VaultDetailScreen(
                    vaultId = vaultId,
                    isEditing = isEditing,
                    onSave = { updatedVault ->
                        scope.launch {
                            vaultId?.let {
                                SQLDelightDB.getVaultRepositoryAsync().updateVault(
                                    it,
                                    updatedVault.name,
                                    updatedVault.dataDir,
                                    updatedVault.mountPoint
                                )
                                isEditing = false
                            }
                        }
                    }
                )
            }
            composable(RencfsScreen.Settings.route) { SettingsScreen() }
            composable(RencfsScreen.About.route) { AboutScreen() }
        }
    }
}