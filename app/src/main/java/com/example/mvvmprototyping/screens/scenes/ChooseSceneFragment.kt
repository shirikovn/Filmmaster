package com.example.mvvmprototyping.screens.scenes

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmprototyping.R
import com.example.mvvmprototyping.itemTouchHelper.scenes.SwipeHelperCallback
import com.example.mvvmprototyping.databinding.FragmentChooseSceneBinding
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.screenViewModel
import com.example.mvvmprototyping.screens.sceneryMode.SceneryModeFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ChooseSceneFragment : BaseFragment() {

    override val viewModel by screenViewModel<ChooseSceneViewModel>()

    class Screen(val film: String): BaseScreen

    private lateinit var binding: FragmentChooseSceneBinding
    private var mItemTouchHelper: ItemTouchHelper? = null
    private lateinit var adapter: ScenesAdapter

    lateinit var scenes: MutableList<DocumentSnapshot>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChooseSceneBinding.inflate(inflater, container, false)

        setAdapter()

        val layoutManager = LinearLayoutManager(requireContext())
        binding.chooseSceneRecyclerView.layoutManager = layoutManager

    //    Log.d("AAA",Calendar.getInstance().timeInMillis.toString())

        binding.topAppBarScenes.setOnMenuItemClickListener {
            if (it.itemId == R.id.add_scene) {
                createNewScene()
                return@setOnMenuItemClickListener true
            }
            false
        }

        binding.bottomNavigationScenes.selectedItemId = R.id.ordinaryMode

        binding.bottomNavigationScenes.setOnNavigationItemSelectedListener{ item ->
            when(item.itemId){
                R.id.ordinaryMode ->
                {
                    true
                }
                R.id.sceneryMode ->
                {
                    viewModel.goToSceneryMode(projectId = viewModel.projectId.value!!)
                    true
                }
                else -> false
            }
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        saveReorderdScenes()
    }

    private fun createNewScene(){
        val projectName = viewModel.projectId.value

        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }
        val viewInflated:View = LayoutInflater.from(context).inflate(R.layout.create_new_scene_dialog, view as ViewGroup, false)
        val editText1:EditText = viewInflated.findViewById(R.id.sceneNameDialog)
        val editText2:EditText = viewInflated.findViewById(R.id.sceneShortDescriptionDialog)

        builder?.setView(viewInflated)
        builder?.setPositiveButton("Create") { dialog, _ ->
            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                .document(projectName!!).collection("scenes").get()
                .addOnSuccessListener {
                    val scenesCounter = it.documents.size

                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                        .document(projectName).collection("scenes")
                        .add(
                            hashMapOf(
                                "N" to scenesCounter,
                                "Title" to editText1.text.toString(),
                                "Scenery" to editText2.text.toString() //todo
                            )
                        ).addOnCompleteListener {
                            updateData()
                        }
                }
        }

        val dialog = builder?.create()
        dialog?.show()
    }

    private fun setAdapter(){
        val projectName = viewModel.projectId.value

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
            .collection("scenes").orderBy("N").get().addOnSuccessListener { querySnapshot ->
                scenes = querySnapshot.documents
                adapter = viewModel.projectId.value?.let { ScenesAdapter(it, viewModel,
                    { setAdapter() }, {reorderScenes()},{updateData()} , scenes)}!!
                binding.chooseSceneRecyclerView.adapter = adapter

                val callback: ItemTouchHelper.Callback = SwipeHelperCallback(adapter)
                mItemTouchHelper = ItemTouchHelper(callback)
                mItemTouchHelper?.attachToRecyclerView(binding.chooseSceneRecyclerView)
            }
    }


    private fun updateData(){
        val projectName = viewModel.projectId.value

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
            .collection("scenes").orderBy("N").get().addOnSuccessListener { querySnapshot ->
                adapter.scenesList = querySnapshot.documents
                reorderScenes()
            }

    }

    private fun reorderScenes(){
        val projectName = viewModel.projectId.value

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
            .collection("scenes").orderBy("N").get().addOnSuccessListener{ query ->
                var counter = 0
                query.documents.forEach{
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName)
                        .collection("scenes").document(it.id).set(
                            hashMapOf(
                                "N" to counter
                            ), SetOptions.merge()
                        )
                    counter++
                }
            }
    }

    private fun saveReorderdScenes(){

        val projectName = viewModel.projectId.value

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
            .collection("scenes").orderBy("N").get().addOnSuccessListener {
                query ->
                scenes.forEach { locDoc ->
                    query.forEach { cloudDoc ->
                        if(locDoc.id == cloudDoc.id){
                            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
                                .collection("scenes").document(cloudDoc.id).set(
                                    hashMapOf(
                                        "N" to scenes.indexOf(locDoc)
                                    ), SetOptions.merge()
                                )
                        }
                    }
                }
            }
    }
}

