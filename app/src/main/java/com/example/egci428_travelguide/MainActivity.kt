package com.example.egci428_travelguide

import android.content.Intent
import android.graphics.Paint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.example.egci428_travelguide.Activity.MapActivity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.signupBtn);
        button.paintFlags = button.paintFlags or Paint.UNDERLINE_TEXT_FLAG
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        fingerprintBtn.setOnClickListener {
            val intent = Intent(this,SigninActivity::class.java)
            startActivity(intent)
        }
        signupBtn.setOnClickListener {
            val intent = Intent(this,SignupActivity::class.java)
            startActivity(intent)
        }
        signinBtn.setOnClickListener {
            var email = InEmailText.text.toString()
            var password = InPasswordText.text.toString()
            if(email!=""&&password!="")
                signIn(email,password)
            else
                Toast.makeText(baseContext, "E-mail and password are required.",
                    Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn(email: String, password: String) {
        // [START sign_in_with_email]
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("Main", "signInWithEmail:success")
                    Toast.makeText(baseContext, "Authentication done.", Toast.LENGTH_SHORT).show()
                    val user = auth.currentUser
                    InEmailText.setText("")
                    InPasswordText.setText("")
                    //val intent = Intent(this,ProfileActivity::class.java)
                    val intent = Intent(this, MapActivity::class.java)
                    startActivity(intent)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("Main", "signInWithEmail:failure", task.exception)
                    Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                }

            }
        // [END sign_in_with_email]

    }

}
