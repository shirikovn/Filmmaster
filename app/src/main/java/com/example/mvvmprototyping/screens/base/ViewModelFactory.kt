package com.example.mvvmprototyping.screens.base

import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.mvvmprototyping.ARG_SCREEN
import com.example.mvvmprototyping.MainViewModel
import com.example.mvvmprototyping.views.Navigator

class ViewModelFactory(
    private val screen: BaseScreen,
    private val fragment: BaseFragment
): ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        val hostActivity = fragment.requireActivity()
        val application = hostActivity.application
        val navigatorProvider = ViewModelProvider(hostActivity,
            ViewModelProvider.AndroidViewModelFactory(application)
        )
        val navigator = navigatorProvider[MainViewModel::class.java]

        val constructor = modelClass.getConstructor(Navigator::class.java, screen::class.java)
        return constructor.newInstance(navigator, screen)
    }
}

inline fun <reified VM:ViewModel> BaseFragment.screenViewModel()= viewModels<VM> {
    val screen = requireArguments().getSerializable(ARG_SCREEN)!! as BaseScreen
    ViewModelFactory(screen, this)
}
