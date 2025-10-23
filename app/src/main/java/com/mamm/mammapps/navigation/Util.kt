package com.mamm.mammapps.navigation

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mamm.mammapps.R
import com.mamm.mammapps.navigation.model.AppRoute
import com.mamm.mammapps.ui.component.icon.BulletedList
import com.mamm.mammapps.ui.component.icon.Child_care
import com.mamm.mammapps.ui.component.icon.Fire
import com.mamm.mammapps.ui.component.icon.Football
import com.mamm.mammapps.ui.component.icon.Puzzle
import com.mamm.mammapps.ui.component.icon.WifiSignal

@Composable
fun GetIconForRoute(route: AppRoute) {
    val iconModifier = Modifier.size(24.dp)
    when (route) {
        AppRoute.HOME -> Icon(Icons.Default.Home, contentDescription = null, modifier = iconModifier)
        AppRoute.EPG -> Icon(BulletedList, contentDescription = null, modifier = iconModifier)
        AppRoute.CHANNELS -> Icon(painterResource(id = R.drawable.menu_channelsicon), contentDescription = null, modifier = iconModifier)
        AppRoute.MOVIES -> Icon(painterResource(id = R.drawable.menu_cinemaicon), contentDescription = null, modifier = iconModifier)
        AppRoute.DOCUMENTARIES -> Icon(painterResource(id = R.drawable.menu_documentariesicon), contentDescription = null, modifier = iconModifier)
        AppRoute.SERIES -> Icon(painterResource(id = R.drawable.menu_serieslogoicon), contentDescription = null, modifier = iconModifier)
        AppRoute.WARNER -> Icon(painterResource(id = R.drawable.menu_wblogoicon), contentDescription = null, modifier = iconModifier)
        AppRoute.ACONTRA -> Icon(painterResource(id = R.drawable.menu_acontralogoicon), contentDescription = null, modifier = iconModifier)
        AppRoute.AMC -> Icon(painterResource(id = R.drawable.menu_amclogoicon), contentDescription = null, modifier = iconModifier)
        AppRoute.SPORTS -> Icon(Football, contentDescription = null, modifier = iconModifier)
        AppRoute.ADULTS -> Icon(Fire, contentDescription = null, modifier = iconModifier)
        AppRoute.KIDS -> Icon(Puzzle, contentDescription = null, modifier = iconModifier)
        AppRoute.SEARCH -> Icon(Icons.Default.Search, contentDescription = null, modifier = iconModifier)
        AppRoute.DIAGNOSTICS -> Icon(WifiSignal, contentDescription = null, modifier = iconModifier)
        AppRoute.LOGOUT -> Icon(Icons.Default.Person, contentDescription = null, modifier = iconModifier) // Puedes usar un icono de logout
        else -> {} // Para otras rutas como Login, etc.
    }
}

 fun getTitleForRoute(route: String): Int {
    return when (route) {
        AppRoute.HOME.route -> R.string.nav_home
        AppRoute.EPG.route -> R.string.nav_epg
        AppRoute.CHANNELS.route -> R.string.nav_channels
        AppRoute.MOVIES.route -> R.string.nav_movies
        AppRoute.DOCUMENTARIES.route -> R.string.nav_documentaries
        AppRoute.SERIES.route -> R.string.nav_series
        AppRoute.WARNER.route -> R.string.nav_warner
        AppRoute.ACONTRA.route -> R.string.nav_acontra
        AppRoute.AMC.route -> R.string.nav_amc
        AppRoute.SPORTS.route -> R.string.nav_sports
        AppRoute.ADULTS.route -> R.string.nav_adults
        AppRoute.KIDS.route -> R.string.nav_kids
        AppRoute.SEARCH.route -> R.string.nav_search
        AppRoute.DIAGNOSTICS.route -> R.string.nav_diagnostics
        AppRoute.LOGOUT.route -> R.string.nav_change_user
        else -> R.string.app_name // Un título por defecto
    }
}