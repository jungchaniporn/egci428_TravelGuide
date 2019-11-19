package com.example.egci428_travelguide.Activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.egci428_travelguide.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_place.*
import java.io.IOException

class AddPlaceActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    internal var storageReference: StorageReference? =null
    private var filePath: Uri? =null
    private val PICK_REQUEST = 2222
//    var i = 0
    var province = ""
    var uid:String = ""
//    var pathArray:ArrayList<Uri>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        val data = intent.extras
        if(data!=null){
            province = data.getString("province")!!
        }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid

        // Initialize Firebase DB
        database = FirebaseDatabase.getInstance().getReference("province/$province/place")

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        importImgBtn.setOnClickListener {
            showFileChooser()
        }
        submitBtn.setOnClickListener {
            savePlace()
        }
    }
    private fun savePlace(){
        var name = placeNameText.text.toString()
        var info = infoText.text.toString()
        var address = addressText.text.toString()
        var contract = contactText.text.toString()

//        for (j in 0..2){
            val imageRef = storageReference!!.child("province/$province/$name")
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
                    placeNameText.setText("")
                    infoText.setText("")
                    addressText.setText("")
                    contactText.setText("")
                    addPlaceImg1.setImageBitmap(null)
                }
//            database.child("$name/info/images/$j").setValue("province/$province/$name"+j)
//        }


        database.child("$name/info/address").setValue(address)
        database.child("$name/info/placeInfo").setValue(info)
        database.child("$name/info/images").setValue("province/$province/$name")
        database.child("$name/info/tel").setValue(contract)
        database.child("$name/info/uid").setValue(uid)


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
            Log.d("firepath",filePath.toString())
//            pathArray!!.add(filePath!!)
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                addPlaceImg2!!.setImageBitmap(bitmap)
//                if(i==0){
//                    addPlaceImg1!!.setImageBitmap(bitmap)
//                    i++
//                }else if(i==1){
//                    addPlaceImg2!!.setImageBitmap(bitmap)
//                    i++
//                }else{
//                    addPlaceImg3!!.setImageBitmap(bitmap)
//                    i=0
//                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
