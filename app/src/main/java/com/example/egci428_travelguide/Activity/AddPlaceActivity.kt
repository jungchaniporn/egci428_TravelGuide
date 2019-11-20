package com.example.egci428_travelguide.Activity

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.egci428_travelguide.DataModel.PlaceInfo
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_place.*
import java.io.*
import java.util.*
import com.google.android.gms.common.util.IOUtils.toByteArray
import android.R.attr.bitmap




class AddPlaceActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    internal var storageReference: StorageReference? =null
    private var filePath: Uri? =null
    private val PICK_REQUEST = 2222
    val REQUEST_IMAGE_CAPTURE = 1
    var imageBitmap:Bitmap? = null
    var imageBitmap1:Bitmap? = null
    var imageBitmap2:Bitmap? = null
    var imageBitmap3:Bitmap? = null
    var i = 0
    var province = ""
    var uid:String = ""
    var imgArray:ArrayList<Bitmap>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.example.egci428_travelguide.R.layout.activity_add_place)
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
        CameraBtn.setOnClickListener {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }
    private fun savePlace(){
        var name = placeNameText.text.toString()
        var info = infoText.text.toString()
        var address = addressText.text.toString()
        var contract = contactText.text.toString()

        for (j in 0..2){
            val baos = ByteArrayOutputStream()
            val imgBitmap:Bitmap
            if(j==0){
                imgBitmap = imageBitmap1!!
            }else if(j==1){
                imgBitmap = imageBitmap2!!
            }else{
                imgBitmap = imageBitmap3!!
            }
            imgBitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val id = UUID.randomUUID().toString()
            val imageRef_camera = storageReference!!.child("province/$province/$name/$id")
            imageRef_camera.putBytes(data)
                .addOnSuccessListener {
                    Toast.makeText(applicationContext, "File uploaded", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener{
                    Toast.makeText(applicationContext, "Failed", Toast.LENGTH_SHORT).show()
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress = 100.0 * taskSnapshot.bytesTransferred/taskSnapshot.totalByteCount
                    Toast.makeText(applicationContext, "Uploaded camera "+progress.toInt()+"%..",Toast.LENGTH_SHORT).show()
                }
            database.child("$name/info/images/$j").setValue("province/$province/$name/$id")
        }

        database.child("$name/info/address").setValue(address)
        database.child("$name/info/placeInfo").setValue(info)
        database.child("$name/info/tel").setValue(contract)
        database.child("$name/info/uid").setValue(uid)

        placeNameText.setText("")
        infoText.setText("")
        addressText.setText("")
        contactText.setText("")
        addPlaceImg1.setImageBitmap(null)
        addPlaceImg2.setImageBitmap(null)
        addPlaceImg3.setImageBitmap(null)
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
                when(i){
                    0 -> {
                        addPlaceImg1!!.setImageBitmap(bitmap)
                        imageBitmap1=bitmap
                        i++
                    }
                    1 -> {
                        addPlaceImg2!!.setImageBitmap(bitmap)
                        imageBitmap2=bitmap
                        i++
                    }
                    2 -> {
                        addPlaceImg3!!.setImageBitmap(bitmap)
                        imageBitmap3=bitmap
                        i=0
                    }
                }
//                imgArray!!.add(imageBitmap_photo!!)
                Log.d("test","test")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data!!.extras!!.get("data") as Bitmap
            when(i){
                0 -> {
                    addPlaceImg1!!.setImageBitmap(imageBitmap)
                    imageBitmap1=imageBitmap
                    i++
                }
                1 -> {
                    addPlaceImg2!!.setImageBitmap(imageBitmap)
                    imageBitmap2=imageBitmap
                    i++
                }
                2 -> {
                    addPlaceImg3!!.setImageBitmap(imageBitmap)
                    imageBitmap3=imageBitmap
                    i=0
                }
            }
//            imgArray!!.add(imageBitmap!!)
        }
    }

}
