package com.example.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object FarmMap : Screen("farm_map")
    object AddRooster : Screen("add_rooster")
    object EscrowOrders : Screen("escrow_orders")
    object ArchitectureBlueprint : Screen("architecture_blueprint")
    object Auth : Screen("auth")
    object UserProfile : Screen("user_profile")

    object RoosterDetail : Screen("rooster_detail/{roosterId}") {
        fun createRoute(roosterId: String) = "rooster_detail/$roosterId"
    }

    object SellerProfile : Screen("seller_profile/{sellerId}") {
        fun createRoute(sellerId: String) = "seller_profile/$sellerId"
    }

    object EncryptedChat : Screen("chat/{roosterId}/{sellerId}") {
        fun createRoute(roosterId: String, sellerId: String) = "chat/$roosterId/$sellerId"
    }

    object EscrowCheckout : Screen("escrow_checkout/{roosterId}") {
        fun createRoute(roosterId: String) = "escrow_checkout/$roosterId"
    }
}
