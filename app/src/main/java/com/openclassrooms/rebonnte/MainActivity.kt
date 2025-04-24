package com.openclassrooms.rebonnte

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.openclassrooms.rebonnte.ui.account.SignInScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleDetailScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.aisle.add.AddAisleScreen
import com.openclassrooms.rebonnte.ui.medicine.detail.MedicineDetailScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.medicine.add.AddMedicineScreen
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    //private lateinit var myBroadcastReceiver: MyBroadcastReceiver

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            MyApp()
        }
        //startBroadcastReceiver()
    }

    /*private fun startMyBroadcast() {
        val intent = Intent("com.rebonnte.ACTION_UPDATE")
        sendBroadcast(intent)
        startBroadcastReceiver()//Create a Memory leak
    }*/

    /*private fun startBroadcastReceiver() {
        myBroadcastReceiver = MyBroadcastReceiver()
        val filter = IntentFilter().apply {
            addAction("com.rebonnte.ACTION_UPDATE")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(myBroadcastReceiver, filter, RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(myBroadcastReceiver, filter)
        }

        Handler().postDelayed({
            startMyBroadcast()
        }, 200)
    }


    class MyBroadcastReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            Toast.makeText(mainActivity, "Update reçu", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        this.unregisterReceiver(myBroadcastReceiver)
    }*/

    companion object {
        lateinit var mainActivity: MainActivity
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = hiltViewModel()
    val aisleViewModel: AisleViewModel = hiltViewModel()



    RebonnteTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        enabled = Firebase.auth.currentUser != null,
                        label = { Text("Aisle") },
                        selected = currentRoute(navController) == "aisle",
                        onClick = { navController.navigate("aisle") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        enabled = Firebase.auth.currentUser != null,
                        label = { Text("Medicine") },
                        selected = currentRoute(navController) == "medicine",
                        onClick = { navController.navigate("medicine") }
                    )
                }
            }
        ) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = "login"
            ) {
                composable("aisle") {
                    AisleScreen(
                        viewModel = aisleViewModel,
                        goToDetail =
                            {
                                navController.navigate("aisleDetail/${it}") {
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            },
                        addAisle =
                            {
                                navController.navigate("addAisle"){
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            },
                        navigateToLogin =
                            {
                                navController.navigate("login") {
                                    // prevent from returning to main screen
                                    popUpTo(navController.graph.id) { inclusive = true }
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            }
                    )
                }
                composable("medicine") {
                    MedicineScreen(
                        viewModel = medicineViewModel,
                        addMedicine =
                            {
                                navController.navigate("addMedicine") {
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            },
                        goToDetail = { idMedicine ->
                            Log.d("NAVIGATION", "Navigating to: medicineDetail/$idMedicine")

                            navController.navigate("medicineDetail/${idMedicine}") {
                                // launch the screen in single top mode so that it is not recreated
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable("login") {
                    SignInScreen(
                        navigateToMedicineScreen =
                            {
                                navController.navigate("aisle") {
                                    // prevent from returning to login screen
                                    popUpTo("login") { inclusive = true }
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            })
                }
                composable("medicineDetail/{medicineId}") { backStackEntry ->
                    Log.d(
                        "NAVIGATION",
                        "Navigating FROM: medicineDetail/${backStackEntry.arguments?.getString("medicineId")}"
                    )

                    val medicineId = backStackEntry.arguments?.getString("medicineId")

                    if (medicineId != null) {
                        MedicineDetailScreen(
                            id = medicineId,
                        )
                    }

                }
                composable("aisleDetail/{aisleName}") { backStackEntry ->
                    val aisleName = backStackEntry.arguments?.getString("aisleName")
                    if (aisleName != null) {
                        AisleDetailScreen(
                            viewModel = medicineViewModel,
                            name = aisleName,
                            navigateToMedicineDetail =
                                { name ->
                                    navController.navigate("medicineDetail/${name}") {
                                        // launch the screen in single top mode so that it is not recreated
                                        launchSingleTop = true
                                    }
                                }
                        )
                    }
                }
                composable("addAisle") {
                    AddAisleScreen(
                        onValidate =
                            {
                                navController.navigate("aisle") {
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            }
                    )
                }
                composable("addMedicine") {
                    AddMedicineScreen(
                        onValidate =
                            {
                                navController.navigate("medicine") {
                                    // launch the screen in single top mode so that it is not recreated
                                    launchSingleTop = true
                                }
                            }
                    )
                }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

