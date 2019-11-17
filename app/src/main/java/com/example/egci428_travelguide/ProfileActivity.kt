package com.example.egci428_travelguide

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_signup.*

class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        updateUI(currentUser)
    }

    private fun updateUI(user: FirebaseUser?) {
        //val Uid = user!!.uid
        if (user != null) {
            MailText.setText(user.email)
            // Initialize Firebase DB
            database = FirebaseDatabase.getInstance().getReference("Users/")
            database.addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                }
                override fun onDataChange(p0: DataSnapshot) {
                    if(p0.exists()){
                        uNameText.text.clear()
                        for (i in p0.children){
                            val user_ = i.getValue(UserInfo::class.java)
                            uNameText.setText(user_!!.username.toString())
                        }
                    }
                }

            })


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
