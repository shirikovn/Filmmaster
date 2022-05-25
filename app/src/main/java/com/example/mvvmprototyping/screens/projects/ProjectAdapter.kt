package com.example.mvvmprototyping.screens.projects

import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mvvmprototyping.databinding.ChooseProjectItemBinding
import com.example.mvvmprototyping.databinding.ChooseProjectNewProjectItemBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.R
import com.google.firebase.storage.ktx.storage
import java.io.IOException
import java.util.*

interface CardsActions{
    fun createNewProject(project: String)
    fun goToScenes(projectId:String)
}

class ProjectAdapter(
    private val cardsActions: CardsActions
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), View.OnClickListener {

    class viewTag(val projectTitle: String, val viewType: Int)
    var itemsCount = 0

    lateinit var projects: MutableList<DocumentSnapshot>

    class ProjectViewHolder(
        val binding: ChooseProjectItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    class CreateNewProjectHolder(
        val binding: ChooseProjectNewProjectItemBinding
    ) : RecyclerView.ViewHolder(binding.root)

    var itemsCount2  =  itemsCount
        set(a){
            field = a
            notifyDataSetChanged()
        }

 //   class dataInTag(val projectId: String, val projectTitle: String, val projectTeam: String)



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        if (viewType == 1) {
            val binding = ChooseProjectItemBinding.inflate(inflater, parent, false)
            return ProjectViewHolder(binding)
        } else {
            val binding = ChooseProjectNewProjectItemBinding.inflate(inflater, parent, false)
            return CreateNewProjectHolder(binding)
        }

    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (this.getItemViewType(position) == 0) {
            (holder as CreateNewProjectHolder).binding.root.setOnClickListener(this)
            return
        }

        (holder as ProjectViewHolder).binding.root.setOnClickListener(this)
        (holder as ProjectViewHolder).binding.optionsProjectList.setOnClickListener(this)

        Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).orderBy("T").get()
            .addOnSuccessListener { result ->
                val first = holder as ProjectViewHolder
                val document = result.documents.asReversed()[position - 1]
                Log.d("ProjectsId",document.id)
                first.binding.projectListTitle.text = document.data!!.getValue("Title").toString()
                first.binding.projectListTeam.text = "Team: " + document.data!!.getValue("Team").toString()

                first.binding.root.tag = document.id
                first.binding.optionsProjectList.tag = viewTag(document.id, 1)

                if(document.data!!.keys.contains("PhotoUrl")) {
                    Firebase.storage.reference.child(
                        document.data!!.getValue("PhotoUrl").toString().replaceFirst("/", "")
                    ).downloadUrl.addOnCompleteListener { result1 ->
                        Glide.with(holder.binding.root).load(result1.result.toString()).centerCrop()
                            .into(first.binding.imageProjectPreview)
                    }
                }

            }
    }

    override fun getItemViewType(position: Int): Int {
        if (position == 0){
            return 0
        } else {
            return 1
        }
    }
    override fun getItemCount(): Int {
        return itemsCount2 + 1
    }

    override fun onClick(p0: View?) {
        if (p0 != null) {
            if (p0.tag != null){
                if (p0.tag is viewTag){
                    val popupMenu = PopupMenu(p0.context, p0)
                    popupMenu.menu.add(0, 1, Menu.NONE, "Rename")
                    popupMenu.menu.add(0, 2, Menu.NONE, "Delete")
                    popupMenu.setOnMenuItemClickListener{
                        when(it.itemId){
                            1 -> {
                                cardsActions.createNewProject((p0.tag as viewTag).projectTitle)
                            }
                            2 -> {
                                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document((p0.tag as viewTag).projectTitle).delete()
                                itemsCount2--
                                notifyDataSetChanged()
                            }
                        }
                        return@setOnMenuItemClickListener true
                    }
                    popupMenu.show()
                    return
                }
                Firebase.firestore.collection(Firebase.auth.currentUser!!.uid).document(p0.tag.toString()).set(
                    hashMapOf(
                        "T" to Calendar.getInstance().timeInMillis
                    ), SetOptions.merge()
                ).addOnCompleteListener {
                    cardsActions.goToScenes(p0.tag.toString())
                }
            } else {
                cardsActions.createNewProject("")
            }
        }
    }

}