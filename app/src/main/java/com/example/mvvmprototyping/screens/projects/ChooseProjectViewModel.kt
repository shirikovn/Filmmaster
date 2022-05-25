package com.example.mvvmprototyping.screens.projects

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmprototyping.databinding.FragmentChooseProjecctBinding
import com.example.mvvmprototyping.screens.auth.AuthFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.projects.create.CreateProjectFragment
import com.example.mvvmprototyping.screens.scenes.ChooseSceneFragment
import com.example.mvvmprototyping.views.Navigator
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.parcelize.Parcelize
import java.security.AuthProvider

class ChooseProjectViewModel(
    private val navigator: Navigator,
    val screen: ChooseProjectFragment.Screen
) : BaseViewModel(), CardsActions{
//    val result = MutableLiveData<String>()
//
//    init {
//        result.value = Firebase.auth.currentUser!!.email
//    }
//
    fun launchAuth() {
    navigator.launch(AuthFragment.Screen())
    }

    fun launchCreateProject(project1: String){
        navigator.launch(CreateProjectFragment.Screen(project1))
    }

    override fun createNewProject(project: String) {
        launchCreateProject(project)
    }

    override fun goToScenes(projectId:String) {
        navigator.launch(ChooseSceneFragment.Screen(projectId))
        //navigator.launch(NavigationFragment.Screen(projectId))
    }

    //
//    override fun onResult(result: Any) {
//        if (result is String){
//            this.result.value = result
//        }
//    }
}