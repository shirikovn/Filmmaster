package com.example.mvvmprototyping.views

import com.example.mvvmprototyping.screens.base.BaseScreen

interface Navigator {

    fun launch(screen: BaseScreen, addToBackStack: Boolean = true)

    fun goBack(result: Any?=null)

}