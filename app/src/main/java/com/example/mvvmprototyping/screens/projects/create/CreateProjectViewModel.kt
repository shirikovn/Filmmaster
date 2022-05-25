package com.example.mvvmprototyping.screens.projects.create

import androidx.lifecycle.MutableLiveData
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.projects.ChooseProjectFragment
import com.example.mvvmprototyping.views.Navigator

class CreateProjectViewModel(private val navigator: Navigator,
                             screen: CreateProjectFragment.Screen
) : BaseViewModel() {
    fun launchChooseProject(){
        navigator.goBack()
    }

    val project1 = MutableLiveData<String>(screen.project)
}