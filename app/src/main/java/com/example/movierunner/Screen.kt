package com.example.movierunner

sealed class Screen(val route: String) {
        object Home : Screen("home")
        object Search : Screen("search")
        object SearchActor : Screen("search_actor")
        object SearchWeb: Screen("search_web")
    }
