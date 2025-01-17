package com.example.egci428_travelguide.Activity

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.egci428_travelguide.*
import com.example.egci428_travelguide.DataModel.PlaceInfo
import com.example.egci428_travelguide.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import com.synnapps.carouselview.CarouselView
import com.synnapps.carouselview.ImageListener
import kotlinx.android.synthetic.main.activity_place_info.*

class PlaceInfoActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var currentUser: FirebaseUser
    lateinit var dataReference: DatabaseReference
    var i = 0
    var province = ""
    var place = ""
    var region =""
    var placeData = PlaceInfo("","","","",ArrayList<String>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_place_info)
        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val data = intent.extras
        if (data != null) {
            province = data.getString("province")!!
            place = data.getString("place")!!
            region = data.getString("region")!!
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
                    val editbtnView = findViewById(R.id.editPlaceBtn) as View
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
                                    //redirect to place list of province page
                                    val intent = Intent(this@PlaceInfoActivity, ProvincePlacesActivity::class.java)
                                    intent.putExtra("province",province)
                                    intent.putExtra("region",region)
                                    startActivity(intent)
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
                    editPlaceBtn.setOnClickListener {
                        val intent = Intent(this@PlaceInfoActivity, AddPlaceActivity::class.java)
                        intent.putExtra("region",region)
                        intent.putExtra("from","placeInfo")
                        intent.putExtra("province",province)
                        intent.putExtra("place",place)
                        intent.putExtra("address",placeData.address)
                        intent.putExtra("placeInfo",placeData.placeInfo)
                        intent.putExtra("tel",placeData.tel)
                        intent.putExtra("images",placeData.images)
                        intent.putExtra("uid",placeData.uid)
                        startActivity(intent)
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
                        if(userData.imageRef!=null && userData.imageRef.isNotEmpty()){
                            val userimgView = findViewById(R.id.userAvatar) as ImageView
                            val storageReference = FirebaseStorage.getInstance().reference
                            //get image
                            storageReference!!.child(userData!!.imageRef)
                                .downloadUrl.addOnSuccessListener {
                                Picasso
                                    .get()
                                    .load(it)
                                    .fit()
                                    .into(userimgView);
                            }.addOnFailureListener {
                                // Handle any errors
                            }
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
        //if the selected place have an image
        val carouselView = findViewById(R.id.carouselView) as CarouselView;
        if(data.images.size>0){
            println("Image : "+ data.images.size)
            var imageListener: ImageListener = object : ImageListener {
                override fun setImageForPosition(position: Int, imageView: ImageView) {
                    val storageReference = FirebaseStorage.getInstance().reference
                    //get image
                    storageReference!!.child(data.images.get(position))
                        .downloadUrl.addOnSuccessListener {
                        Picasso
                            .get()
                            .load(it)
                            .into(imageView)
                    }.addOnFailureListener {
                        // Handle any errors
                        println("Set image unsuccessful")
                    }

                }
            }
            carouselView.setImageListener(imageListener)
            carouselView.setPageCount(data.images.size)
        }else{
            //in case no image, don't show carousel
            carouselView.visibility = View.GONE
        }
    }
    override fun onResume() {
        // once user leaves the page and comeback, the app will require fingerprint authentication
        super.onResume()
        if(i!=0){
            val keyguardmng= getSystemService(Context.KEYGUARD_SERVICE)
                    as KeyguardManager
            val fingerprintmng = getSystemService(Context.FINGERPRINT_SERVICE)
                    as FingerprintManager
            val fingerprintauth = FingerprintAuth(this, keyguardmng, fingerprintmng)
            if (fingerprintauth.checkLockScreen()) {
                fingerprintauth.generateKey()
                if (fingerprintauth.initCipher()) {
                    fingerprintauth.cipher.let {
                        fingerprintauth.cryptoObject = FingerprintManager.CryptoObject(it)
                    }
                    val helper = FingerprintHelper(this)
                    if (fingerprintauth.fingerprintManager != null && fingerprintauth.cryptoObject != null) {
                        helper.startAuth(fingerprintauth.fingerprintManager, fingerprintauth.cryptoObject)
                    }
                }
            }
        }
        i++
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_detail,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.profileItem){
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("parent","PlaceInfo")
            intent.putExtra("province",province)
            intent.putExtra("region",region)
            intent.putExtra("place",place)
            startActivity(intent)
            finish()
        }else if(id == R.id.signoutItem){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == android.R.id.home){
            val intent = Intent(this, ProvincePlacesActivity::class.java)
            intent.putExtra("province",province)
            intent.putExtra("region",region)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
