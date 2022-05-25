package com.example.mvvmprototyping.screens.scenes

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmprototyping.itemTouchHelper.scenes.ItemTouchHelperAdapter
import com.example.mvvmprototyping.databinding.ChooseSceneItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import java.util.*

class ScenesAdapter(
    val project: String,
    val navigator: SceneCardsActions,
    val onSwiped : () -> Unit,
    val onSwiped2 : () -> Unit,
    val onSwiped3 : () -> Unit,
    scenesList: List<DocumentSnapshot>
) : RecyclerView.Adapter<ScenesAdapter.SceneViewHolder>(), View.OnClickListener,
    ItemTouchHelperAdapter {

    var scenesList = scenesList
    set(value) {
        field = value
        notifyDataSetChanged()
    }

    interface SceneCardsActions{
        fun goToShoots(projectId:String, scene:String)
    }

    class SceneViewHolder(
        val binding: ChooseSceneItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    lateinit var parent: ViewGroup

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SceneViewHolder {
        this.parent = parent
        val inflater = LayoutInflater.from(parent.context)
        val binding = ChooseSceneItemBinding.inflate(inflater, parent, false)
        return SceneViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SceneViewHolder, position: Int) {
        holder.binding.root.setOnClickListener(this)

        val document = scenesList[position]
        holder.binding.sceneName.text = document.data!!.getValue("Title").toString()
        holder.binding.shortDescription.text = document.data!!.getValue("Scenery").toString()
        holder.binding.scenePosition.setText("0${document.data!!["N"].toString()}")

      //  holder.binding.scenePosition.setOnClickListener(this)

        holder.binding.root.tag = document.id

        if(document.data!!.keys.contains("PhotoUrl")) {
            Firebase.storage.reference.child(
                document.data!!.getValue("PhotoUrl").toString().replaceFirst("/", "")
            ).downloadUrl.addOnCompleteListener { result1 ->
                Glide.with(holder.binding.root).load(result1.result.toString()).centerCrop()
                    .into(holder.binding.scenePreviewImageView)
            }
        }
    }

    override fun onClick(p0: View?) {

        navigator.goToShoots(project, p0!!.tag.toString())

    }

    override fun getItemCount(): Int {
        return scenesList.size
    }

    override fun onItemMove(fromPosition: Int, toPosition: Int): Boolean {
        if (fromPosition < toPosition) {
            for (i in fromPosition until toPosition) {
                Collections.swap(scenesList, i, i + 1)
            }
        } else {
            for (i in fromPosition downTo toPosition + 1) {
                Collections.swap(scenesList, i, i - 1)
            }
        }
        notifyItemMoved(fromPosition, toPosition)

        return true
    }



    override fun onItemDismiss(position: Int) {

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(project)
            .collection("scenes").orderBy("N").get().addOnSuccessListener {
                val doc = it.documents[position].id
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(project)
                    .collection("scenes").document(doc).delete().addOnSuccessListener {
                        onSwiped3()
                    }
            }
    }


}