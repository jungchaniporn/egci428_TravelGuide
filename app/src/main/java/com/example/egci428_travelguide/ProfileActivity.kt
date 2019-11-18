package com.example.egci428_travelguide

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_signup.*
import android.widget.EditText



class ProfileActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var Ed_uName: EditText? = null
    var Ed_Mail: EditText? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()

        val currentUser = auth.currentUser
        updateUI(currentUser)

        editBtn.setOnClickListener {
            Ed_uName = findViewById(R.id.uNameText)
            Ed_uName!!.setEnabled(true)
            Ed_Mail = findViewById(R.id.MailText)
            Ed_Mail!!.setEnabled(true)
        }
    }

    private fun updateUI(user: FirebaseUser?) {
        val Uid = user!!.uid
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
                        for (i in p0.children){
                            val user_ = i.getValue()
                                uNameText.setText(user_.toString())

                        }
                    }
                }
            })
        }
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
