package com.example.mvvmprototyping.screens.scenes

import androidx.lifecycle.MutableLiveData
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.projects.ChooseProjectFragment
import com.example.mvvmprototyping.screens.sceneryMode.SceneryModeFragment
import com.example.mvvmprototyping.screens.shoots.ManageShootsFragment
import com.example.mvvmprototyping.views.Navigator

class ChooseSceneViewModel(
    private val navigator: Navigator,
    val screen: ChooseSceneFragment.Screen
) : BaseViewModel(),ScenesAdapter.SceneCardsActions {
    val projectId = MutableLiveData<String>()

    override fun goToShoots(projectId: String, scene: String) {
        navigator.launch(ManageShootsFragment.Screen(projectId, scene))
    }

    init {
        projectId.value = screen.film
    }

    fun goToSceneryMode(projectId: String) {
        navigator.launch(SceneryModeFragment.Screen(projectId))
    }
}