package com.example.egci428_travelguide.Activity

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.fingerprint.FingerprintManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
import android.widget.EditText
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import com.bumptech.glide.Glide
import com.example.egci428_travelguide.*
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profile.*
import kotlin.collections.ArrayList


class AddPlaceActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    private lateinit var storage: FirebaseStorage
    internal var storageReference: StorageReference? =null
    private var filePath: Uri? =null
    private val PICK_REQUEST = 2222
    val REQUEST_IMAGE_CAPTURE = 1
    var imageBitmap:Bitmap? = null
    var imgView = ArrayList<ImageView>()
    var imgBitmap = ArrayList<Bitmap>()
    var i = 0
    var ii =0
    var region = ""
    var province = ""
    var uid:String = ""
    var place = ""
    var from = ""
    var placeData =  PlaceInfo("","","","",ArrayList<String>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        uid = auth.currentUser!!.uid
        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference

        imgView.add(addPlaceImg1!!)
        imgView.add(addPlaceImg2!!)
        imgView.add(addPlaceImg3!!)
        val data = intent.extras
        if(data!=null){
            var Ed_name: EditText? = null
            from = data.getString("from")!!
            when(from){
                "provincePlace" ->{
                    Log.d("add", "from ProvincePlace")
                    province = data.getString("province")!!
                    region = data.getString("region")!!
                }
                "placeInfo" -> {
                    textView6.setText("Edit Place")
                    Log.d("add", "from PlaceInfo")
                    region = data.getString("region")!!
                    province = data.getString("province")!!
                    place = data.getString("place")!!
                    placeData!!.address = data.getString("address")!!
                    placeData!!.placeInfo = data.getString("placeInfo")!!
                    placeData!!.tel = data.getString("tel")!!
                    placeData!!.images = data.getStringArrayList("images")!!
                    placeData!!.uid = data.getString("uid")!!
                    placeNameText.setText(place)
                    Ed_name = findViewById(R.id.placeNameText)
                    Ed_name!!.setEnabled(false)
                    infoText.setText(placeData!!.placeInfo)
                    addressText.setText(placeData!!.address)
                    contactText.setText(placeData!!.tel)
                    var imgSize = placeData.images.size-1
                    for (n in 0..imgSize){
                        storageReference!!.child(placeData.images.get(n))
                            .downloadUrl.addOnSuccessListener {
                            Picasso
                                .get()
                                .load(it)
                                .into(imgView[n])
//                            var temp = Bitmap.createBitmap(imgView[n].getWidth(), imgView[n].getHeight(), Bitmap.Config.RGB_565)
                            if(imgView[n].getDrawable()!=null){
                                var temp = imgView[n].getDrawable().toBitmap()
                                imgBitmap.add(temp)
                            }

                        }.addOnFailureListener {
                            // Handle any errors
                            println("Set image unsuccessful")
                        }

                    }
                }
            }

        }
        // Initialize Firebase DB
        database = FirebaseDatabase.getInstance().getReference("province/$province/place")

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
        var bitmapSize = imgBitmap.size-1
        if(name!="" && info!="" && address!=""&&contract!=""){
        if(bitmapSize > 0){
        for (j in 0..bitmapSize){
            val baos = ByteArrayOutputStream()
            imgBitmap[j].compress(Bitmap.CompressFormat.JPEG, 100, baos)
            val data = baos.toByteArray()
            val id = UUID.randomUUID().toString()
            var imageRef_camera:StorageReference? = null
            if(from == "placeInfo"){
                println("Index: "+j)
                imageRef_camera = storageReference!!.child(placeData.images[j])
            }else{
                imageRef_camera = storageReference!!.child("province/$province/$name/$id")
            }
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
                if(from == "placeInfo"){
                    database.child("$name/info/images/$j").setValue(placeData.images[j])

                }else{
                    database.child("$name/info/images/$j").setValue("province/$province/$name/$id")
                }

            }
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
        }else{
            Toast.makeText(baseContext, "All field except the image are required", Toast.LENGTH_SHORT).show()
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
            Log.d("firepath",filePath.toString())
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imgView[i].setImageBitmap(bitmap)
                imgBitmap.removeAt(0)
                imgBitmap.add(bitmap!!)
                i++
                if (i==3){
                    i = 0
                }
                Log.d("test","test")
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            imageBitmap = data!!.extras!!.get("data") as Bitmap
            imgView[i].setImageBitmap(imageBitmap)
            imgBitmap.removeAt(0)
            imgBitmap.add(imageBitmap!!)
            i++
            if (i==3){
                i = 0
            }
        }
    }
    override fun onResume() {
        super.onResume()
        if(ii!=0){
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
        ii++
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_detail,menu)

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.getItemId()
        if(id == R.id.profileItem){
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }else if(id == R.id.signoutItem){
            auth.signOut()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == android.R.id.home){
            var intent = Intent()
            if(from == "placeInfo"){//case edit
                intent = Intent(this, PlaceInfoActivity::class.java)
            }else{//case add
                intent = Intent(this, ProvincePlacesActivity::class.java)
            }
            intent.putExtra("province",province)
            intent.putExtra("region",region)
            intent.putExtra("place",place)
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }

}
