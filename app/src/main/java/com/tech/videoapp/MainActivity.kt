package com.tech.videoapp

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tech.videoapp.navigation.AppNavigation
import com.tech.videoapp.ui.theme.VideoAppTheme
import com.tech.videoapp.ui_layer.screens.RequestPermission
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            VideoAppTheme {
                Scaffold {
                    Box(
                        modifier = Modifier.padding(it)
                    ) {
                        RequestPermission{
                            if (it){
                                AppNavigation()
                            }else{
                                Toast.makeText(this@MainActivity, "Please Provide the permission of Storage", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }
    }
}
