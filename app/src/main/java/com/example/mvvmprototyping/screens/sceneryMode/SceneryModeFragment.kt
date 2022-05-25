package com.example.mvvmprototyping.screens.sceneryMode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmprototyping.R
import com.example.mvvmprototyping.databinding.FragmentSceneryModeBinding
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.BaseViewModel
import com.example.mvvmprototyping.screens.base.screenViewModel
import com.example.mvvmprototyping.screens.scenes.ChooseSceneViewModel
import com.example.mvvmprototyping.screens.scenes.ScenesAdapter
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SceneryModeFragment: BaseFragment() {
    override val viewModel by screenViewModel<SceneryModeViewModel>()

    class Screen(val film: String): BaseScreen

    lateinit var binding: FragmentSceneryModeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSceneryModeBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.sceneryModeRecyclerView.layoutManager = layoutManager

        val projectName = viewModel.film
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName)
            .collection("scenes").orderBy("N").get().addOnSuccessListener { querySnapshot ->
                val scenes = querySnapshot.documents
                val adapter = SceneryModeAdapter(projectName, scenes)
                binding.sceneryModeRecyclerView.adapter = adapter
            }


        binding.bottomNavigationSceneryMode.selectedItemId = R.id.sceneryMode
        binding.bottomNavigationSceneryMode.setOnNavigationItemSelectedListener{ item ->
           // val transition = requireActivity().supportFragmentManager.beginTransaction()

            when(item.itemId){
                R.id.ordinaryMode ->
                {
                    requireActivity().onBackPressed()
                    //viewModel.goToScenes()
                    true
                }
                R.id.sceneryMode ->
                {
                    true
                }
                else -> false
            }
        }

        return binding.root
    }
}