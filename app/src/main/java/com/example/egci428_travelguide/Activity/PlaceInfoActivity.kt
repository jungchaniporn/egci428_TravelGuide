package com.example.egci428_travelguide.Activity

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.egci428_travelguide.DataModel.Place
import com.example.egci428_travelguide.DataModel.PlaceInfo
import com.example.egci428_travelguide.DataSource.ProvincePlacesDataSource
import com.example.egci428_travelguide.R
import com.example.egci428_travelguide.UserInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_place_info.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_province_places.*

class PlaceInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    lateinit var dataReference: DatabaseReference
    var province = ""
    var place = ""
    var placeData = PlaceInfo("","","","",ArrayList<String>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)
        val data = intent.extras
        if (data != null) {
            province = data.getString("province")!!
            place = data.getString("place")!!
        }
        placeName.setText(place)
        //check current user
        auth = FirebaseAuth.getInstance()
        currentUser = auth.currentUser!!

        //fetch data from database
        dataReference =
            FirebaseDatabase.getInstance().getReference("province/$province/place")
        val dataListener = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if(dataSnapshot.exists()){
                    placeData = dataSnapshot.getValue(PlaceInfo::class.java)!!
                    updateData(placeData!!)
                }
                if (placeData!!.uid == currentUser!!.uid) {//current user is the post owner
                    val editbtnView = findViewById(R.id.editBtn) as View
                    val deletebtnView = findViewById(R.id.deleteBtn) as View
                    //show edit and delete button
                    editbtnView.visibility = View.VISIBLE
                    deletebtnView.visibility = View.VISIBLE
                    deleteBtn.setOnClickListener {
                        // Initialize a new instance of
                        val builder = AlertDialog.Builder(this@PlaceInfoActivity)

                        // Set the alert dialog title
                        builder.setTitle("Delete Confirmation")

                        // Display a message on alert dialog
                        builder.setMessage("Would you like to delete the post?")

                        // Set a positive button and its click listener on alert dialog
                        builder.setPositiveButton("YES") { dialog, which ->
                            // Do something when user press the positive button
                            val deleteRef = FirebaseDatabase.getInstance()
                                .getReference("province/$province/place")
                            println("DElete set Place :"+place)
                            deleteRef.child(place).removeValue()
                                .addOnCompleteListener {
                                    //delete successful
                                    println("Successfully delete")
                                    Toast.makeText(
                                        applicationContext,
                                        "Post deleted",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    finish()
                                    println("delete donnnnneeeeee")
                                }
                                .addOnFailureListener {
                                    //delete fail
                                    Toast.makeText(
                                        applicationContext,
                                        "Deletion failed. Please try again.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                        }

                        // Display a negative button on alert dialog
                        builder.setNegativeButton("CANCEL") { dialog, which ->
                            Toast.makeText(
                                applicationContext,
                                "Cancelled action",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        // Finally, make the alert dialog using builder
                        val dialog: AlertDialog = builder.create()

                        // Display the alert dialog on app interface
                        dialog.show()
                    }
                } else {
                    val placenameView = findViewById(R.id.placeName) as View
                    placenameView.setLayoutParams(
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                    )
                }
                val userRef = FirebaseDatabase.getInstance().getReference("Users/" + placeData.uid)
                val userDataListener = object : ValueEventListener {
                    override fun onDataChange(dataSnapshot: DataSnapshot) {
                        val userData = dataSnapshot.getValue(UserInfo::class.java)
                        usernameInfo.setText(userData!!.username)
                        val userimgView = findViewById(R.id.userAvatar) as ImageView
                        val storageReference = FirebaseStorage.getInstance().reference
                        //get image
                        storageReference!!.child(userData!!.imageRef)
                            .downloadUrl.addOnSuccessListener {
                            Picasso
                                .get()
                                .load(it)
                                .into(userimgView);
                        }.addOnFailureListener {
                            // Handle any errors
                        }
                    }

                    override fun onCancelled(databaseError: DatabaseError) {
                        // Getting Post failed, log a message
                        println(databaseError)
                    }
                }
                userRef.addListenerForSingleValueEvent(userDataListener)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                println(databaseError)
            }
        }
        dataReference.child(place).child("info").addValueEventListener(dataListener)
    }

    private fun updateData(data: PlaceInfo) {
        placeInfo.setText(data.placeInfo)
        addressInfo.setText(data.address)
        contactInfo.setText(data.tel)
    }
}
