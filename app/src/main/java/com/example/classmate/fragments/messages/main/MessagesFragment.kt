package com.example.classmate.fragments.messages.main

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import android.content.SharedPreferences
import com.google.android.material.floatingactionbutton.FloatingActionButton
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import com.example.classmate.R
import androidx.recyclerview.widget.LinearLayoutManager
import android.content.Intent
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.fragment.app.Fragment
import com.example.classmate.databinding.FragmentMessagesBinding
import com.example.classmate.fragments.profile.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import java.util.ArrayList

class MessagesFragment : Fragment() {

    private var binding: FragmentMessagesBinding? = null

    var auth: FirebaseAuth? = null
    var firestore: FirebaseFirestore? = null
    var storage: FirebaseStorage? = null
    var storageReference: StorageReference? = null

    @get:JvmName("getAdapterContext")
    var context: Context? = null

    var preferences: SharedPreferences? = null

    var button: FloatingActionButton? = null
    var searchButton: FloatingActionButton? = null
    var joinButton: FloatingActionButton? = null
    var addForumButton: FloatingActionButton? = null
    var contactsRV: RecyclerView? = null
    var emptyDefaultTV: TextView? = null
    var arrowIcon: ImageView? = null

    var adapter: ForumsAdapter? = null

    private var userID: String? = null
    var forums: ArrayList<String>? = null
    var clicked: Boolean = false
    var showAnim: Boolean = false

    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_open
        )
    }
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.rotate_close
        )
    }
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.from_bottom
        )
    }
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.to_bottom
        )
    }

    private val arrowUp: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.arrow_up
        )
    }
    private val arrowDown: Animation by lazy {
        AnimationUtils.loadAnimation(
            requireContext(),
            R.anim.arrow_down
        )
    }


    var launcher =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult? -> pullFromDatabase() }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMessagesBinding.inflate(inflater, container, false)
        val root: View = binding!!.root

        context = requireContext()

        requireActivity().title = "Contacts"

        firebase()
        setAnimations()
        setSharedPreferences()
        setResourceObjects(root)
        setListeners()
        pullFromDatabase()
        setRecyclerView()

        return root
    }

    private fun setAnimations() {
        rotateOpen.duration = 400
        rotateClose.duration = 300
        fromBottom.duration = 400
        toBottom.duration = 300
        arrowUp.duration = 600
        arrowDown.duration = 600
        arrowUp.repeatCount = (Animation.INFINITE)
        arrowDown.repeatCount = Animation.INFINITE
    }

    private fun firebase() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        userID = auth!!.uid
        if (userID == null) requireActivity().finish()
    }

    private fun setSharedPreferences() {
        preferences = context!!.getSharedPreferences("com.example.classmate", Context.MODE_PRIVATE)
    }

    private fun setResourceObjects(root: View) {
        button = root.findViewById(R.id.add_new_contact_button)
        searchButton = root.findViewById(R.id.search_forum_button)
        joinButton = root.findViewById(R.id.join_private_button)
        contactsRV = root.findViewById(R.id.contacts_recycler_view)
        addForumButton = root.findViewById(R.id.create_new_forum_button)
        emptyDefaultTV = root.findViewById(R.id.clubs_empty_text_view)
        arrowIcon = root.findViewById(R.id.clubs_down_arrow_icon)
    }

    fun setListeners() {
        button!!.setOnClickListener { expand() }
        searchButton!!.setOnClickListener { findContact() }
        joinButton!!.setOnClickListener { joinPrivate() }
        addForumButton!!.setOnClickListener { newForum() }
    }

    private fun newForum() {
        expand()
        val intent = Intent(requireContext(), NewForumActivity::class.java)
        expand()
        launcher.launch(intent)
    }

    private fun joinPrivate() {
        expand()
        val editText = EditText(requireContext())
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Enter club code: ")
        builder.setIcon(R.drawable.ic_baseline_lock_24)
        builder.setView(editText)
        builder.setPositiveButton("Join") { dialog, _ ->
            join(editText.text.toString(), dialog)
        }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
        builder.show()
    }

    private fun join(code: String, dialog: DialogInterface) {
        firestore!!.collection("forums").get()
            .addOnSuccessListener { snapshot -> findWithKey(snapshot, code, dialog) }
    }

    private fun findWithKey(snapshot: QuerySnapshot, code: String, dialog: DialogInterface) {
        for (data in snapshot) {
            val forum = Forum.from(data.data)
            if (forum.key == code) {
                forums!!.add(forum.id)
                firestore!!.collection("users").document(userID!!).get()
                    .addOnSuccessListener { docSnap: DocumentSnapshot ->
                        onSuccess(
                            docSnap,
                            forum,
                            dialog
                        )
                    }
                break
            }
        }
    }

    private fun onSuccess(snapshot: DocumentSnapshot, forum: Forum, dialog: DialogInterface) {
        if (snapshot.data == null) return
        val user = User.from(snapshot.data!!)
        user.forums.add(forum.id)
        firestore!!.collection("users").document(snapshot.id).set(user)
            .addOnSuccessListener { adapter!!.notifyDataSetChanged(); dialog.cancel() }
    }

    private fun setRecyclerView() {
        contactsRV!!.adapter = adapter
        contactsRV!!.layoutManager = LinearLayoutManager(context)
    }

    private fun expand() {
        hideAnim()
        clicked = !clicked
        arrowIcon!!.visibility = View.INVISIBLE
        searchButton!!.visibility = if (clicked) View.VISIBLE else View.INVISIBLE
        joinButton!!.visibility = if (clicked) View.VISIBLE else View.INVISIBLE
        addForumButton!!.visibility = if (clicked) View.VISIBLE else View.INVISIBLE
        if (clicked) {
            button!!.startAnimation(rotateOpen)
            joinButton!!.startAnimation(fromBottom)
            searchButton!!.startAnimation(fromBottom)
            addForumButton!!.startAnimation(fromBottom)
        } else {
            button!!.startAnimation(rotateClose)
            joinButton!!.startAnimation(toBottom)
            searchButton!!.startAnimation(toBottom)
            addForumButton!!.startAnimation(toBottom)
        }
    }

    private fun findContact() {
        expand()
        startActivityForResult(Intent(context, FindForumActivity::class.java), 1)
    }

    private fun pullFromDatabase() {
        firestore!!.collection("users").document(userID!!).get()
            .addOnSuccessListener { snapshot: DocumentSnapshot -> successfulPull(snapshot) }
            .addOnFailureListener { failedCall() }
    }

    private fun successfulPull(snapshot: DocumentSnapshot) {
        if (snapshot.data == null) {
            this.forums = ArrayList()
            return
        }
        val (_, _, _, _, _, forums) = User.from(snapshot.data!!)
        this.forums = forums
        adapter = ForumsAdapter(requireActivity(), this.forums, userID, false)
        setRecyclerView()
        showAnim = forums.size == 0
        emptyDefaultTV!!.visibility = if (showAnim) View.VISIBLE else View.INVISIBLE
        arrowIcon!!.visibility = if (showAnim) View.VISIBLE else View.INVISIBLE
        if (showAnim) animateArrow()
        else hideAnim()
    }

    private fun animateArrow() {
        arrowIcon!!.startAnimation(arrowDown)
        arrowUp.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                if (showAnim)
                    arrowIcon!!.startAnimation(arrowDown)
                else hideAnim()
            }
        })
        arrowDown.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(arg0: Animation) {}
            override fun onAnimationRepeat(arg0: Animation) {}
            override fun onAnimationEnd(arg0: Animation) {
                if (showAnim)
                    arrowIcon!!.startAnimation(arrowUp)
                else
                    hideAnim()
            }
        })
    }

    private fun hideAnim() {
        showAnim = false
        arrowIcon!!.clearAnimation()
        arrowIcon!!.visibility = View.GONE
    }

    private fun failedCall() {
        forums = ArrayList()
        val show = forums!!.size == 0
        emptyDefaultTV!!.visibility = if (show) View.VISIBLE else View.INVISIBLE
        arrowIcon!!.visibility = if (show) View.VISIBLE else View.INVISIBLE
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        pullFromDatabase()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}