package com.example.egci428_travelguide

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
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
import com.example.egci428_travelguide.Activity.*
import com.example.egci428_travelguide.DataModel.PlaceInfo
import com.example.egci428_travelguide.DataSource.RegionDataSource
import kotlinx.android.synthetic.main.activity_province_list.*
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
    var i = 0
    var parent = ""
    var region = ""
    var province = ""
    var place = ""
    var from = ""
    var placeData =  PlaceInfo("","","","",ArrayList<String>())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val data = intent.extras
        // retrieve data from other pages
        if(data!=null){
            parent = data.getString("parent")!!
            //different pages have different variable to retrieve
            when(parent){
                "List"-> {
                    region = data.getString("region")!!
                }
                "Place"->{
                    region = data.getString("region")!!
                    province = data.getString("province")!!
                }
                "PlaceInfo"->{
                    region = data.getString("region")!!
                    province = data.getString("province")!!
                    place = data.getString("place")!!
                }
                "AddEdit"->{
                    region = data.getString("region")!!
                    province = data.getString("province")!!
                    place = data.getString("place")!!
                    from = data.getString("from")!!
                    placeData!!.address = data.getString("address")!!
                    placeData!!.placeInfo = data.getString("placeInfo")!!
                    placeData!!.tel = data.getString("tel")!!
                    placeData!!.images = data.getStringArrayList("images")!!
                    placeData!!.uid = data.getString("uid")!!
                }
            }

        }
        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance()
        // Initialize Firebase Storage
        storage = FirebaseStorage.getInstance()
        storageReference = storage!!.reference
        // get infomation from current user
        val currentUser = auth.currentUser
        Uid = currentUser!!.uid
        updateUI(currentUser)
        loadImage(this)

        editBtn.setOnClickListener {
            //enable editText
            Ed_uName = findViewById(R.id.uNameText)
            Ed_uName!!.setEnabled(true)
            Ed_img = findViewById(R.id.imgBtn)
            Ed_img!!.setEnabled(true)
            Ed_save = findViewById(R.id.saveBtn)
            Ed_save!!.setEnabled(true)
        }
        imgBtn.setOnClickListener {
            // prevent fingerprint authentication to check when get back from camera roll
            i = 0
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
            // upload file image to firebase storage
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
        // update username in database
        database.child("/username").setValue(username)
            .addOnCompleteListener {
                Toast.makeText(applicationContext, "Updated", Toast.LENGTH_SHORT).show()
            }
        // update img in storage
        uploadFile()
        //disable editText
        Ed_uName!!.setEnabled(false)
        Ed_img!!.setEnabled(false)
        Ed_save!!.setEnabled(false)
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            MailText.setText(user.email)
            // prepare database
            database = FirebaseDatabase.getInstance().getReference("Users/"+Uid)
            // get infomation from database that referring to
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
                    //get image path refer to storage
                    Log.d("LoadImg",user_!!.imageRef)
                    // dowload image according to the path from database
                    storageReference!!.child(user_!!.imageRef).downloadUrl.addOnSuccessListener {
                        Log.d("Link",it.toString())
                        Glide.with(context)
                            .load(it)
                            .into(profileImg)
//                        profileImg.setImageURI(it)
                    }.addOnFailureListener {
                        // Handle any errors
                        Log.d("Error",it.toString())
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
        // check the id of button that is clicked
        if(id == R.id.profileItem){
            Toast.makeText(this,"Currently at the profile page",Toast.LENGTH_SHORT).show()
        }else if(id == R.id.signoutItem){
            auth.signOut()
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }else if(id == android.R.id.home){
            var intent:Intent? = null
            // send back all the information that parent page need which will be different
            when(parent){
                "Map"-> {
                    intent = Intent(this,MapActivity::class.java)
                }
                "List"-> {
                    intent = Intent(this,ProvinceListActivity::class.java)
                    intent.putExtra("region",region)
                }
                "Place"->{
                    intent = Intent(this,ProvincePlacesActivity::class.java)
                    intent.putExtra("region",region)
                    intent.putExtra("province",province)
                }
                "PlaceInfo"->{
                    intent = Intent(this,PlaceInfoActivity::class.java)
                    intent.putExtra("region",region)
                    intent.putExtra("province",province)
                    intent.putExtra("place",place)
                }
                "AddEdit"->{
                    intent = Intent(this,AddPlaceActivity::class.java)
                    intent.putExtra("from",from)
                    intent.putExtra("region",region)
                    intent.putExtra("province",province)
                    intent.putExtra("place",place)
                    intent.putExtra("address",placeData.address)
                    intent.putExtra("placeInfo",placeData.placeInfo)
                    intent.putExtra("tel",placeData.tel)
                    intent.putExtra("images",placeData.images)
                    intent.putExtra("uid",placeData.uid)
                }

            }
            startActivity(intent)
            finish()
        }

        return super.onOptionsItemSelected(item)
    }
    override fun onResume() {
        // once user leaves the page and comeback, the app will require fingerprint authentication
        super.onResume()
        if(i!=0){
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
        i++
    }

}
