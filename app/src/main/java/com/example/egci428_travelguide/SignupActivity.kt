package com.example.egci428_travelguide

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_signup.*
import java.io.File
import java.io.IOException
import java.util.*

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    internal var storageReference: StorageReference? =null
    private var filePath: Uri? =null
    private val PICK_REQUEST = 1234
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        // back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase DB
        database = FirebaseDatabase.getInstance().getReference("Users")
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        importPhotoBtn.setOnClickListener {
            showFileChooser()
        }
        submitSigupBtn.setOnClickListener {
            var email = emailText.text.toString()
            var password = passwordText.text.toString()

            createAccount(email,password)

        }
    }

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
    }
    private fun createAccount(email: String, password: String) {

        // [START create_user_with_email]
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Create Account", "createUserWithEmail:success")
                    Toast.makeText(baseContext, "Authentication done.",
                        Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    //store extra information in database
                    database.child(user!!.uid+"/username")
                        .setValue(usernameText.text.toString()) //set username
                        .addOnCompleteListener {
                            Toast.makeText(applicationContext, "Message saved successfully", Toast.LENGTH_SHORT).show()
                        }
                    // store info in storage
                    uploadFile(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Create Account", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
    }

    private fun uploadFile(user:FirebaseUser){
        if (filePath != null){
            Toast.makeText(applicationContext, "Uploading...", Toast.LENGTH_SHORT).show()
            val id = UUID.randomUUID().toString()
            val imageRef = storageReference!!.child("Users/"+user!!.uid+"/profileImg/"+id)
            // upload file
            imageRef.putFile(filePath!!)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "File uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    Toast.makeText(applicationContext, "Uploaded "+progress.toInt()+"%..",Toast.LENGTH_SHORT).show()
                    usernameText.setText("")
                    emailText.setText("")
                    passwordText.setText("")
                    imageView.setImageBitmap(null)
                }
            database.child(user!!.uid+"/filePath").setValue(filePath.toString()) //set username
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "File path saved successfully", Toast.LENGTH_SHORT).show()
                }
            database.child(user!!.uid+"/imageRef").setValue("Users/"+user!!.uid+"/profileImg/"+id) //set username
        }
    }

    private fun showFileChooser() {
        val intent = Intent()
//        any type of img file
        intent.type  = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
//        ForResult for returning img back to app
//        PICK_IMAGE_REQUEST = id for returning each page must be diff
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null){
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                // show in imgView
                imageView!!.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
//        get abck to home page
        if(id == android.R.id.home){
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}
