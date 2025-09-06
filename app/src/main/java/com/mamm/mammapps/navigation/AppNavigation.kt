package com.mamm.mammapps.navigation

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mamm.mammapps.data.model.Event
import com.mamm.mammapps.data.model.VoD
import com.mamm.mammapps.ui.mapper.toContentDetailUI
import com.mamm.mammapps.ui.screen.DetailScreen
import com.mamm.mammapps.ui.screen.HomeScreen
import com.mamm.mammapps.ui.screen.LoginScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("login") {
            LoginScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
//        composable("home") {
//            HomeScreen(
//                onContentClicked = { content ->
//                    navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
//                    navController.navigate("detail") {
//                        launchSingleTop = true
//                    }
//                }
//            )
//        }
//
//        composable("detail") { _ ->
//            val contentItem = navController.previousBackStackEntry
//                ?.savedStateHandle
//                ?.get<Any>("content")
//
//            contentItem?.let {
//                DetailScreen(
//                    content = when (it) {
//                        is VoD -> it.toContentDetailUI()
//                        is Event -> it.toContentDetailUI()
//                        else -> return@composable
//                    },
//                    onPlayClick = { TODO() }
//                )
//            }
//        }

        composable("home") {
            HomeScreen(
                onContentClicked = { content ->
                    Log.d("Navigation", "HOME: Navegando a detail con content: $content")
                    Log.d("Navigation", "HOME: Current backstack antes: ${navController.currentBackStackEntry?.destination?.route}")

                    navController.navigate("detail") {
                        launchSingleTop = true
                    }

                    Log.d("Navigation", "HOME: Current backstack después: ${navController.currentBackStackEntry?.destination?.route}")
                    navController.currentBackStackEntry?.savedStateHandle?.set("content", content)
                    Log.d("Navigation", "HOME: Data seteada en savedStateHandle")
                }
            )
        }

        composable("detail") { backStackEntry ->
            Log.d("Navigation", "DETAIL: ===== COMPOSABLE EJECUTÁNDOSE =====")

            val contentItem = remember(backStackEntry) {
                val content = backStackEntry.savedStateHandle.get<Any>("content")
                Log.d("Navigation", "DETAIL: Content en remember: $content")
                content
            }

            Log.d("Navigation", "DETAIL: Content final: $contentItem")

            contentItem?.let {
                Log.d("Navigation", "DETAIL: Mostrando DetailScreen")
                DetailScreen(
                    content = when (it) {
                        is VoD -> it.toContentDetailUI()
                        is Event -> it.toContentDetailUI()
                        else -> return@composable
                    },
                    onPlayClick = { TODO() }
                )
            } ?: run {
                Log.d("Navigation", "DETAIL: No hay contenido")
                Text("No content available")
            }
        }



    }
}