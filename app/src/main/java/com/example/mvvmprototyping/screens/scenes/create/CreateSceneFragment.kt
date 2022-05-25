package com.example.mvvmprototyping.screens.scenes.create

import android.net.Uri
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.base.screenViewModel

class CreateSceneFragment : BaseFragment() {
    override val viewModel by screenViewModel<CreateSceneViewModel>()


}