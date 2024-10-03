package com.randos.doctorsapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.randos.doctorsapp.presentation.feature.appointment.AppointmentScreen
import com.randos.doctorsapp.presentation.feature.developer.contact.ContactScreen
import com.randos.doctorsapp.presentation.feature.developer.filedownload.FileDownloadScreen
import com.randos.doctorsapp.presentation.feature.developer.funground.FunGroundScreen
import com.randos.doctorsapp.presentation.feature.developer.locationstream.LocationStreamScreen
import com.randos.doctorsapp.presentation.feature.doctorlist.DoctorListScreen
import com.randos.doctorsapp.presentation.feature.home.HomeScreen
import com.randos.doctorsapp.presentation.feature.login.LoginScreen
import com.randos.doctorsapp.presentation.feature.otp.OtpScreen
import com.randos.doctorsapp.presentation.feature.splash.SplashScreen

@Composable
fun DoctorsAppNavGraph() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                moveToLoginScreen = {
                    navController.navigate("login") {
                        popUpTo("splash") { inclusive = true }
                    }
                },
                moveToHomeScreen = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                })
        }

        composable("login") {
            LoginScreen(onSuccessfulLogin = { navController.navigate("otp") })
        }

        composable("doctor_list") {
            DoctorListScreen(onItemClick = { navController.navigate("appointment") })
        }

        composable("otp") {
            OtpScreen(onConfirmation = {
                navController.navigate("home") {
                    popUpTo("login") { inclusive = true }
                }
            })
        }

        composable("appointment") {
            AppointmentScreen(onAppointmentConfirmation = {
                navController.popBackStack("home", false)
            })
        }

        composable("home") {
            HomeScreen(
                onDoctorListClick = { navController.navigate("doctor_list") },
                onMyAppointmentClick = { },
                onFunGroundClick = { navController.navigate("fun_ground") })
        }

        composable("fun_ground") {
            FunGroundScreen(onMoveToFileDownload = { navController.navigate("file_download") })
        }

        composable("file_download") {
            FileDownloadScreen(onMoveToLocationStream = { navController.navigate("location_stream") })
        }

        composable("location_stream") {
            LocationStreamScreen {
                navController.navigate("contact")
            }
        }

        composable("contact") {
            ContactScreen()
        }
    }
}