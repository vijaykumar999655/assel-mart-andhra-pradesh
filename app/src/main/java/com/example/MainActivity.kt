package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ui.navigation.Screen
import com.example.ui.screens.AddRoosterScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.EncryptedChatScreen
import com.example.ui.screens.EscrowPaymentScreen
import com.example.ui.screens.FarmMapScreen
import com.example.ui.screens.MarketplaceHomeScreen
import com.example.ui.screens.RoosterDetailScreen
import com.example.ui.screens.SellerProfileScreen
import com.example.ui.screens.TechnicalArchitectureScreen
import com.example.ui.screens.UserProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.MarketplaceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AseelMartApp()
                }
            }
        }
    }
}

@Composable
fun AseelMartApp() {
    val navController = rememberNavController()
    val viewModel: MarketplaceViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            MarketplaceHomeScreen(
                viewModel = viewModel,
                onRoosterClick = { roosterId ->
                    navController.navigate(Screen.RoosterDetail.createRoute(roosterId))
                },
                onOpenMap = {
                    navController.navigate(Screen.FarmMap.route)
                },
                onAddRooster = {
                    navController.navigate(Screen.AddRooster.route)
                },
                onOpenArchitecture = {
                    navController.navigate(Screen.ArchitectureBlueprint.route)
                },
                onOpenProfile = {
                    navController.navigate(Screen.UserProfile.route)
                }
            )
        }

        composable(
            route = Screen.RoosterDetail.route,
            arguments = listOf(navArgument("roosterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roosterId = backStackEntry.arguments?.getString("roosterId") ?: ""
            RoosterDetailScreen(
                roosterId = roosterId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onOpenChat = { rId, sId ->
                    navController.navigate(Screen.EncryptedChat.createRoute(rId, sId))
                },
                onOpenEscrow = { rId ->
                    navController.navigate(Screen.EscrowCheckout.createRoute(rId))
                },
                onOpenSellerProfile = { sId ->
                    navController.navigate(Screen.SellerProfile.createRoute(sId))
                },
                onOpenMapToFarm = {
                    navController.navigate(Screen.FarmMap.route)
                }
            )
        }

        composable(Screen.FarmMap.route) {
            FarmMapScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSelectSeller = { sellerId ->
                    navController.navigate(Screen.SellerProfile.createRoute(sellerId))
                }
            )
        }

        composable(
            route = Screen.SellerProfile.route,
            arguments = listOf(navArgument("sellerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
            SellerProfileScreen(
                sellerId = sellerId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onRoosterClick = { roosterId ->
                    navController.navigate(Screen.RoosterDetail.createRoute(roosterId))
                }
            )
        }

        composable(Screen.AddRooster.route) {
            AddRoosterScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EncryptedChat.route,
            arguments = listOf(
                navArgument("roosterId") { type = NavType.StringType },
                navArgument("sellerId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val roosterId = backStackEntry.arguments?.getString("roosterId") ?: ""
            val sellerId = backStackEntry.arguments?.getString("sellerId") ?: ""
            EncryptedChatScreen(
                roosterId = roosterId,
                sellerId = sellerId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EscrowCheckout.route,
            arguments = listOf(navArgument("roosterId") { type = NavType.StringType })
        ) { backStackEntry ->
            val roosterId = backStackEntry.arguments?.getString("roosterId") ?: ""
            EscrowPaymentScreen(
                roosterId = roosterId,
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.ArchitectureBlueprint.route) {
            TechnicalArchitectureScreen(
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Auth.route) {
            AuthScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onAuthSuccess = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.UserProfile.route) {
            UserProfileScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
                onNavigateToAuth = {
                    navController.navigate(Screen.Auth.route)
                },
                onNavigateToAddRooster = {
                    navController.navigate(Screen.AddRooster.route)
                }
            )
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyApplicationTheme { Greeting("Android") }
}
