package com.example.mvvmprototyping.screens.auth

import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.projects.ChooseProjectFragment
import com.example.mvvmprototyping.views.Navigator

class AuthViewModel(
    private val navigator: Navigator,
    screen: AuthFragment.Screen
): BaseViewModel() {
    fun launchChooseProject(){
        navigator.launch(ChooseProjectFragment.Screen(), false)
    }


}