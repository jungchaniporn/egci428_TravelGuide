package com.example.egci428_travelguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_signup.*

class SignupActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        // back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase DB
        database = FirebaseDatabase.getInstance().getReference("Users")

        submitSigupBtn.setOnClickListener {
            var email = emailText.text.toString()
            var password = passwordText.text.toString()
            var username = usernameText.text.toString()
            createAccount(email,password)
            val Uid = auth.currentUser!!.uid
        // keep data in firebase
            database.child(Uid+"/username")
                .setValue(username)
                .addOnCompleteListener {
                    Toast.makeText(applicationContext, "Message saved successfully", Toast.LENGTH_SHORT).show()
                }
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
                    usernameText.setText("")
                    emailText.setText("")
                    passwordText.setText("")
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Create Account", "createUserWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        // [END create_user_with_email]
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
