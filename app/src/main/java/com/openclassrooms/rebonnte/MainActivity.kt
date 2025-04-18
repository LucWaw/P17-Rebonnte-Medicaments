package com.openclassrooms.rebonnte

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.openclassrooms.rebonnte.ui.aisle.AisleScreen
import com.openclassrooms.rebonnte.ui.aisle.AisleViewModel
import com.openclassrooms.rebonnte.ui.medicine.MedicineScreen
import com.openclassrooms.rebonnte.ui.medicine.MedicineViewModel
import com.openclassrooms.rebonnte.ui.theme.RebonnteTheme

class MainActivity : ComponentActivity() {

    //private lateinit var myBroadcastReceiver: MyBroadcastReceiver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainActivity = this
        setContent {
            MyApp()
        }
        //startBroadcastReceiver()//Create a Memory leak
    }

    /*private fun startMyBroadcast() {
        val intent = Intent("com.rebonnte.ACTION_UPDATE")
        sendBroadcast(intent)
        startBroadcastReceiver()
    }

    private fun startBroadcastReceiver() {
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApp() {
    val navController = rememberNavController()
    val medicineViewModel: MedicineViewModel = viewModel()
    val aisleViewModel: AisleViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val route = navBackStackEntry?.destination?.route

    RebonnteTheme {
        Scaffold(
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Aisle") },
                        selected = currentRoute(navController) == "aisle",
                        onClick = { navController.navigate("aisle") }
                    )
                    NavigationBarItem(
                        icon = { Icon(Icons.AutoMirrored.Filled.List, contentDescription = null) },
                        label = { Text("Medicine") },
                        selected = currentRoute(navController) == "medicine",
                        onClick = { navController.navigate("medicine") }
                    )
                }
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    if (route == "medicine") {
                        medicineViewModel.addRandomMedicine(aisleViewModel.aisles.value)
                    } else if (route == "aisle") {
                        aisleViewModel.addRandomAisle()
                    }
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            }
        ) {
            NavHost(
                modifier = Modifier.padding(it),
                navController = navController,
                startDestination = "aisle"
            ) {
                composable("aisle") { AisleScreen(aisleViewModel) }
                composable("medicine") { MedicineScreen(medicineViewModel) }
            }
        }
    }
}

@Composable
fun currentRoute(navController: NavController): String? {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    return navBackStackEntry?.destination?.route
}

