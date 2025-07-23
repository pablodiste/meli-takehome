package android.meli.navigation

import android.meli.feature.articledetails.articleDetailsScreen
import android.meli.feature.articlelist.articleListScreen
import android.meli.feature.routes.Destination
import android.meli.feature.routes.Destination.ArticleList
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ArticleList) {
        articleListScreen(
            onNavigateToDetails = { id ->
                navController.navigate(Destination.ArticleDetails(id))
            },
        )
        articleDetailsScreen(
            onNavigateBack = {
                navController.popBackStack()
            }
        )
    }
}

