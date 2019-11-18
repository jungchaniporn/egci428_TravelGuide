package com.example.egci428_travelguide

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import android.widget.EditText
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import android.graphics.BitmapFactory
import android.graphics.Bitmap
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_signup.*


//import sun.jvm.hotspot.ci.ciObjectFactory.getMetadata




class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    internal var storageReference: StorageReference? =null
    var Ed_uName: EditText? = null
    var Ed_Mail: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        val currentUser = auth.currentUser
        val Uid = currentUser!!.uid
        updateUI(currentUser,Uid)
        loadImage(this)

        editBtn.setOnClickListener {
//            not save data yet, just enable-disable editText
            Ed_uName = findViewById(R.id.uNameText)
            Ed_uName!!.setEnabled(true)
            Ed_Mail = findViewById(R.id.MailText)
            Ed_Mail!!.setEnabled(true)
        }

    }

    private fun updateUI(user: FirebaseUser?, uid:String) {
        if (user != null) {
            MailText.setText(user.email)
            // Initialize Firebase DB
            database = FirebaseDatabase.getInstance().getReference("Users/"+uid)
            database.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        uNameText.text.clear()
                        val user_ = p0.getValue(UserInfo::class.java)
                        uNameText.setText(user_!!.username)
                    }
                }
            })
        }
    }
    private fun loadImage(context: Context){
        val currentUser = auth.currentUser
        val Uid = currentUser!!.uid
        database = FirebaseDatabase.getInstance().getReference("Users/"+Uid)
        database.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists()){
                    val user_ = p0.getValue(UserInfo::class.java)
                    Log.d("LoadImg",user_!!.imageRef)
                    storageReference!!.child(user_!!.imageRef).downloadUrl.addOnSuccessListener {
                        Log.d("Link",it.toString())
                        Glide.with(context)
                            .load(it)
                            .into(profileImg);
//                        profileImg.setImageURI(it)
                    }.addOnFailureListener {
                        // Handle any errors
                    }
                }
            }
        })


    }
// for action bar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_detail,menu)

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.profileItem){
            val intent = Intent(this,ProfileActivity::class.java)
            startActivity(intent)
        }else if(id == R.id.signoutItem){
            auth.signOut()
            finish()
        }else if(id == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
