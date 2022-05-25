package com.example.mvvmprototyping.screens.shoots

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mvvmprototyping.R
import com.example.mvvmprototyping.databinding.FragmentManageShootsBinding
import com.example.mvvmprototyping.itemTouchHelper.shots.DragHelperCallbackForShots
import com.example.mvvmprototyping.screens.base.BaseFragment
import com.example.mvvmprototyping.screens.base.BaseScreen
import com.example.mvvmprototyping.screens.base.screenViewModel
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.util.*

class ManageShootsFragment() : BaseFragment() {
    private val PICK_IMAGE_REQUEST = 22
    private var mItemTouchHelper: ItemTouchHelper? = null
    lateinit var filePath: Uri

    override val viewModel by screenViewModel<ManageShootsViewModel>()

    lateinit var binding: FragmentManageShootsBinding

    class Screen(val film: String, val scene: String): BaseScreen

    val storageRef = Firebase.storage.reference
    val imagesRef: StorageReference = storageRef.child("images")

    lateinit var imageUrl: StorageReference
    lateinit var adapter: ShootsAdapter

    lateinit var projectId: String

    lateinit var shots: MutableList<DocumentSnapshot>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentManageShootsBinding.inflate(inflater, container,false)

        setAdapter()

        val layoutManager = LinearLayoutManager(requireContext())

        binding.recyclerView.layoutManager = layoutManager


        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
            .document(viewModel.projectId.value!!).collection("scenes")
            .document(viewModel.scene.value!!).get().addOnSuccessListener {
                binding.topAppBarShoots.title = it["Title"].toString()
            }
        //binding.topAppBarShoots.title = viewModel.scene.value


        binding.topAppBarShoots.setOnMenuItemClickListener {
                if (it.itemId == R.id.add_shoot) {
                    createNewShot ()
                    saveReorderdScenes()
                    return@setOnMenuItemClickListener true

                } else if (it.itemId == R.id.rename_shoots){

                    val builder: AlertDialog.Builder? = activity?.let {
                        AlertDialog.Builder(it)
                    }
                    val viewInflated:View = LayoutInflater.from(context).inflate(R.layout.rename_scene_dialog, view as ViewGroup, false)
                    val editText:EditText = viewInflated.findViewById(R.id.renameShootTextField)
                    editText.hint = "Scene title"
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                        .document(viewModel.projectId.value!!).collection("scenes")
                        .document(viewModel.scene.value!!).get().addOnSuccessListener {
                            editText.setText(it["Title"].toString())

                        }
                    builder?.setView(viewInflated)
                    builder!!.setOnDismissListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                            .document(viewModel.projectId.value!!).collection("scenes")
                            .document(viewModel.scene.value!!).set(
                                hashMapOf(
                                    "Title" to editText.text.toString()
                                ), SetOptions.merge()
                            )
                        binding.topAppBarShoots.title = editText.text.toString()
                    }

                    val dialog = builder?.create()
                    dialog?.show()

                }  else if (it.itemId == R.id.change_description_shoots){

                    val builder: AlertDialog.Builder? = activity?.let {
                        AlertDialog.Builder(it)
                    }
                    val viewInflated:View = LayoutInflater.from(context).inflate(R.layout.rename_scene_dialog, view as ViewGroup, false)
                    val editText:EditText = viewInflated.findViewById(R.id.renameShootTextField)
                    editText.hint = "Short Description"
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                        .document(viewModel.projectId.value!!).collection("scenes")
                        .document(viewModel.scene.value!!).get().addOnSuccessListener {
                            editText.setText(it["Scenery"].toString())
                        }
                    builder?.setView(viewInflated)
                    builder!!.setOnDismissListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                            .document(viewModel.projectId.value!!).collection("scenes")
                            .document(viewModel.scene.value!!).set(
                                hashMapOf(
                                    "Scenery" to editText.text.toString()
                                ), SetOptions.merge()
                            )
                    }
                    val dialog = builder?.create()
                    dialog?.show()
                }
            false
        }

        return binding.root
    }

    override fun onPause() {
        super.onPause()
        saveReorderdScenes()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST
            && resultCode == Activity.RESULT_OK
            && data != null
            && data.getData() != null) {

            // Get the Uri of data
            filePath = data.getData()!!

            imageUrl = imagesRef.child(UUID.randomUUID().toString())
            imageUrl.putFile(filePath).addOnSuccessListener {


                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                    .document(viewModel.projectId.value!!).collection("scenes")
                    .document(viewModel.scene.value!!).collection("shoots")
                    .document(projectId)
                    .set(
                        hashMapOf(
                            "PhotoUrl" to imageUrl.path
                        ), SetOptions.merge()
                    )
                    .addOnCompleteListener {
                        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                            .document(viewModel.projectId.value!!).collection("scenes")
                            .document(viewModel.scene.value!!).collection("shoots").get()
                            .addOnSuccessListener { result ->
                                setAdapter()
                            }
                    }

            }
        }

    }

    fun SelectImage(projectId: String) {

        this.projectId = projectId
        // Defining Implicit Intent to mobile gallery
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(
                intent,
                "Select Image from here..."
            ),
            PICK_IMAGE_REQUEST
        )
    }

    fun setAdapter(){
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
            .document(viewModel.projectId.value!!).collection("scenes")
            .document(viewModel.scene.value!!).collection("shoots").orderBy("N").get().addOnSuccessListener {
                shots = it.documents
                val adapter1 = ShootsAdapter(viewModel.projectId.value!!, viewModel.scene.value!!, this, shots)
                binding.recyclerView.adapter = adapter1

                val callback: ItemTouchHelper.Callback = DragHelperCallbackForShots(adapter1)
                mItemTouchHelper = ItemTouchHelper(callback)
                mItemTouchHelper?.attachToRecyclerView(binding.recyclerView)
            }
    }

    fun reorderScenes(){
        val projectName = viewModel.projectId.value

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
            .collection("scenes").document(viewModel!!.scene!!.value!!).collection("shoots").orderBy("N").get().addOnSuccessListener{ query ->
                var counter = 0
                query.documents.forEach{
                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName)
                        .collection("scenes").document(viewModel!!.scene!!.value!!).collection("shoots").document(it.id).set(
                            hashMapOf(
                                "N" to counter
                            ), SetOptions.merge()
                        ) .addOnSuccessListener {
                            if (counter == query.size() ){
                                setAdapter()
                            }
                        }
                    counter++
                }
            }
    }


    fun saveReorderdScenes(){

        val projectName = viewModel.projectId.value

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
            .collection("scenes").document(viewModel.scene.value!!).collection("shoots").orderBy("N").get().addOnSuccessListener {
                    query ->
                shots.forEach { locDoc ->
                    query.forEach { cloudDoc ->
                        if(locDoc.id == cloudDoc.id){
                            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(projectName!!)
                                .collection("scenes").document(viewModel.scene.value!!).collection("shoots").document(cloudDoc.id).set(
                                    hashMapOf(
                                        "N" to shots.indexOf(locDoc)
                                    ), SetOptions.merge()
                                )
                        }
                    }
                }
            }
    }

    fun createNewShot(){
        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(viewModel.projectId.value!!).collection("scenes").document(viewModel.scene.value!!).collection("shoots").get().addOnSuccessListener {
            val projectsCount = it.documents.size
            Log.d("Bababa", projectsCount.toString())
            Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
                .document(viewModel.projectId.value!!).collection("scenes")
                .document(viewModel.scene.value!!).collection("shoots").document().set(
                    hashMapOf(
                        "N" to projectsCount
                    )
                ).addOnCompleteListener {
//                    Firebase.firestore.collection(Firebase.auth.currentUser!!.uid)
//                        .document(viewModel.projectId.value!!).collection("scenes")
//                        .document(viewModel.scene.value!!).collection("shoots").get()
//                        .addOnSuccessListener { result ->
                        setAdapter()
 //               }
                }
        }
    }
}