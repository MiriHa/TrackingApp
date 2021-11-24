package com.example.trackingapp.util

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.trackingapp.R
import java.security.InvalidParameterException

enum class ScreenType {Welcome, SignUpLogin, HomeScreen}

fun Fragment.navigate(to: ScreenType, from: ScreenType) {
    if(to == from){
        throw InvalidParameterException("Can't navigate to same screen type!")
    }

    var navOptions = NavOptions.Builder().build()

    if(from == ScreenType.Welcome || from == ScreenType.SignUpLogin){
        Log.d("xxx","fromLoginScreen, dont pop back")
       navOptions = NavOptions.Builder().setPopUpTo(R.id.nav_graph, true).build()
    }

    when(to){
        ScreenType.Welcome -> {
            findNavController().navigate(R.id.OnBoardingFragment,null, navOptions)
        }
        ScreenType.SignUpLogin -> {
            findNavController().navigate(R.id.loginFragment, null, navOptions)
        }
        ScreenType.HomeScreen -> {
            findNavController().navigate(R.id.homeScreenFragment,null, navOptions)
        }
    }

}