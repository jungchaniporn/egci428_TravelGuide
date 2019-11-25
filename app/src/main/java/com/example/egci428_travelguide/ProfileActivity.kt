package com.example.egci428_travelguide

import android.app.Activity
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
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import android.provider.MediaStore
import android.widget.Button
import android.widget.Toast
import java.io.IOException


//import sun.jvm.hotspot.ci.ciObjectFactory.getMetadata




class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    internal var storageReference: StorageReference? =null
    var Ed_uName: EditText? = null
    var Ed_img: Button? = null
    var Ed_save: Button? = null
    var Uid:String? = null
    var imgRef:String? = null
    private val PICK_REQUEST = 1111
    private var filePath: Uri? =null


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
        Uid = currentUser!!.uid
        updateUI(currentUser)
        loadImage(this)

        editBtn.setOnClickListener {
//            not save data yet, just enable-disable editText
            Ed_uName = findViewById(R.id.uNameText)
            Ed_uName!!.setEnabled(true)
            Ed_img = findViewById(R.id.imgBtn)
            Ed_img!!.setEnabled(true)
            Ed_save = findViewById(R.id.saveBtn)
            Ed_save!!.setEnabled(true)
        }
        imgBtn.setOnClickListener {
            showFileChooser()
        }
        saveBtn.setOnClickListener {
            if(uNameText.text.toString()!="")
                UpdateUser(uNameText.text.toString())
            else
                Toast.makeText(baseContext, "Username is required.",
                    Toast.LENGTH_SHORT).show()
        }

    }
    private fun uploadFile(){
        if (filePath != null){
            Toast.makeText(applicationContext, "Uploading...", Toast.LENGTH_SHORT).show()
            val img = storageReference!!.child(imgRef.toString())
            // upload file
            img.putFile(filePath!!)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "File uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    Toast.makeText(applicationContext, "Uploaded "+progress.toInt()+"%..",Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun showFileChooser() {
        val intent = Intent()
        intent.type  = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                // show in imgView
                profileImg!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun UpdateUser(username: String) {
        // update username in DB
        database.child("/username").setValue(username)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
            }
        // update img in storage
        uploadFile()
        Ed_uName!!.setEnabled(false)
        Ed_img!!.setEnabled(false)
        Ed_save!!.setEnabled(false)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            MailText.setText(user.email)
            // Initialize Firebase DB
            database = FirebaseDatabase.getInstance().getReference("Users/"+Uid)
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
                    imgRef = user_!!.imageRef
                    Log.d("LoadImg",user_!!.imageRef)
                    storageReference!!.child(user_!!.imageRef).downloadUrl.addOnSuccessListener {
                        Log.d("Link",it.toString())
                        Glide.with(context)
                            .load(it)
                            .into(profileImg)
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
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
        }else if(id == android.R.id.home){
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
