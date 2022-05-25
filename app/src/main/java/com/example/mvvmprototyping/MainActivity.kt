package com.example.mvvmprototyping

import android.app.Application
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.*
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.mvvmprototyping.databinding.ActivityMainBinding
import com.example.mvvmprototyping.databinding.FragmentAuthBinding
import com.example.mvvmprototyping.screens.auth.AuthFragment
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.projects.ChooseProjectFragment

class MainActivity : AppCompatActivity() {

    val activityMainViewModel by viewModels<MainViewModel> { AndroidViewModelFactory(Application()) }

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater).also{ setContentView(it.root) }

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        if(savedInstanceState == null){
            activityMainViewModel.launchFragment(this, AuthFragment.Screen(), addToBackStack = false)
        }


        supportFragmentManager.registerFragmentLifecycleCallbacks(fragmentCallbacks, false)
    }

    override fun onResume() {
        super.onResume()
        activityMainViewModel.whenActivityActive.mainActivity = this
    }

    override fun onPause() {
        super.onPause()
        activityMainViewModel.whenActivityActive.mainActivity = null
    }

    override fun onDestroy() {
        super.onDestroy()
        supportFragmentManager.unregisterFragmentLifecycleCallbacks(fragmentCallbacks)
    }

    private val fragmentCallbacks = object : FragmentManager.FragmentLifecycleCallbacks(){
        override fun onFragmentCreated(
            fm: FragmentManager,
            f: Fragment,
            savedInstanceState: Bundle?
        ) {
            val result = activityMainViewModel.result.value?.getValue() ?: return

            if (f is BaseFragment){
                f.viewModel.onResult(result)
            }
        }
    }
}