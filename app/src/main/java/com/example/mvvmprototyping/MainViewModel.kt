package com.example.mvvmprototyping

import android.app.Application
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.views.Navigator
import com.example.mvvmprototyping.views.UiActions

const val ARG_SCREEN = "ARG_SCREEN"

class MainViewModel(
    application: Application
): AndroidViewModel(application), Navigator
{

    val whenActivityActive = MainActivityActions()

    private val _result = MutableLiveData<EventMy<Any>>()
    val result: LiveData<EventMy<Any>> = _result

    override fun launch(screen: BaseScreen, addToBackStack1: Boolean) {
        whenActivityActive{
            launchFragment(it, screen, addToBackStack = addToBackStack1)
        }
    }

    override fun goBack(result: Any?) {
        whenActivityActive{
            if (result != null){
                _result.value = EventMy(result)
            }
            it.onBackPressed()
        }
    }

    fun launchFragment(activity: MainActivity, screen: BaseScreen, addToBackStack:Boolean = true){
        val fragment = screen.javaClass.enclosingClass.newInstance() as Fragment
        fragment.arguments = bundleOf(ARG_SCREEN to screen)
        val transition = activity.supportFragmentManager.beginTransaction()
        if (addToBackStack){
            transition.addToBackStack(null)
        }
        transition
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}