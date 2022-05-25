package com.example.mvvmprototyping.screens.base

import androidx.lifecycle.ViewModel

open class BaseViewModel : ViewModel(){
    open fun onResult(result: Any){}
}