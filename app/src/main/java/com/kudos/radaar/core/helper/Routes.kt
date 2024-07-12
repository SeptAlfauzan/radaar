package com.kudos.radaar.core.helper

sealed class Routes(val route: String) {
    object Home: Routes(route = "/home")
    object Radar: Routes(route = "/radar")
}