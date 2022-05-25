package com.example.mvvmprototyping.screens.sceneryMode

import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.projects.ChooseProjectFragment
import com.example.mvvmprototyping.screens.scenes.ChooseSceneFragment
import com.example.mvvmprototyping.views.Navigator

class SceneryModeViewModel(
    private val navigator: Navigator,
    val screen: SceneryModeFragment.Screen
) : BaseViewModel() {
    val film = screen.film

    fun goToScenes(){
        //navigator.launch(ChooseSceneFragment.Screen(film))

    }
}