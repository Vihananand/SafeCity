package com.example.safecity.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Register : Screen("register", "Register", Icons.Default.AppRegistration)
    object Dashboard : Screen("dashboard", "Home", Icons.Default.Home)
    object Map : Screen("map", "Map", Icons.Default.Map)
    object Community : Screen("community", "Community", Icons.Default.Groups)
    object Report : Screen("report", "Report", Icons.Default.AddLocation)
    object Emergency : Screen("emergency", "SOS", Icons.Default.Emergency)
    object Alerts : Screen("alerts", "Alerts", Icons.Default.Notifications)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object WalkingMode : Screen("walking_mode", "Walking", Icons.Default.DirectionsWalk)
    object Guardians : Screen("guardians", "Guardians", Icons.Default.Shield)
    object IncidentDetail : Screen("incident_detail", "Incident Detail", Icons.Default.Info)
    object Vault : Screen("vault", "Media Vault", Icons.Default.PhotoLibrary)
    object Training : Screen("training", "Safety Quiz", Icons.Default.Quiz)
    object Scan : Screen("scan", "Safety Scan", Icons.Default.WifiTethering)
    object Havens : Screen("havens", "Safe Havens", Icons.Default.Apartment)
    object SafetyPlan : Screen("safety_plan", "Safety Plan", Icons.Default.FactCheck)
    object Recorder : Screen("recorder", "Recorder", Icons.Default.Mic)
}
