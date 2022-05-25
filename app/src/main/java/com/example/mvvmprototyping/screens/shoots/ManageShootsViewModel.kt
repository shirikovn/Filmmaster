package com.example.mvvmprototyping.screens.shoots

import androidx.lifecycle.MutableLiveData
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.scenes.ChooseSceneFragment
import com.example.mvvmprototyping.views.Navigator

class ManageShootsViewModel(
    private val navigator: Navigator,
    val screen: ManageShootsFragment.Screen
) : BaseViewModel() {
    val projectId = MutableLiveData<String>()
    val scene = MutableLiveData<String>()

    init {
        projectId.value = screen.film
        scene.value = screen.scene
    }
}