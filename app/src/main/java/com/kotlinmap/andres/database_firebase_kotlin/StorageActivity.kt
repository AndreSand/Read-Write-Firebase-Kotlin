package com.kotlinmap.andres.database_firebase_kotlin

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.Toast
import com.firebase.ui.database.FirebaseRecyclerAdapter
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.kotlinmap.andres.database_firebase_kotlin.Model.UploadInfo
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_storage.*
import kotlinx.android.synthetic.main.item_image.view.*

class StorageActivity : AppCompatActivity(), View.OnClickListener {

    private val TAG = "StorageActivity"
    //track Choosing Image Intent
    private val CHOOSING_IMAGE_REQUEST = 1234

    private var fileUri: Uri? = null

    private var dataReference: DatabaseReference? = null
    private var imageReference: StorageReference? = null
    private var fileRef: StorageReference? = null

    private var mAdapter: FirebaseRecyclerAdapter<UploadInfo, ImgViewHolder>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_storage)

        tvFileLoad.text = ""
        tvFileLoad.visibility = View.GONE

        dataReference = FirebaseDatabase.getInstance().getReference("images")
        imageReference = FirebaseStorage.getInstance().reference.child("images")

        btn_choose_file.setOnClickListener(this)
        btn_upload_file.setOnClickListener(this)
        btn_back.setOnClickListener(this)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = false
        rcvListImg.setHasFixedSize(true)
        rcvListImg.layoutManager = layoutManager

        val query = dataReference!!.limitToLast(3)

        mAdapter = object : FirebaseRecyclerAdapter<UploadInfo, ImgViewHolder>(
                UploadInfo::class.java, R.layout.item_image, ImgViewHolder::class.java, query) {

            override fun populateViewHolder(viewHolder: ImgViewHolder?, model: UploadInfo?, position: Int) {
                viewHolder!!.itemView.tvImgName.text = model!!.name
                Picasso.with(this@StorageActivity)
                        .load(model.url)
                        .error(R.drawable.common_google_signin_btn_icon_dark)
                        .into(viewHolder.itemView.imgView)

            }
        }

        rcvListImg.adapter = mAdapter
    }

    override fun onClick(view: View?) {
        val i = view!!.id

        when (i) {
            R.id.btn_choose_file -> showChoosingFile()
            R.id.btn_upload_file -> uploadFile()
            R.id.btn_back -> finish()
        }

    }

    private fun uploadFile() {
        if (fileUri != null) {
            val fileName = edtFileName.text.toString()

            if (!validateInputFileName(fileName)) {
                return
            }

            tvFileLoad.visibility = View.VISIBLE

            fileRef = imageReference!!.child(fileName + "." + getFileExtension(fileUri!!))
            fileRef!!.putFile(fileUri!!)
                    .addOnSuccessListener { taskSnapshot ->
                        val name = taskSnapshot.metadata!!.name
                        val url = taskSnapshot.downloadUrl.toString()

                        Log.e(TAG, "Uri: " + taskSnapshot.downloadUrl)
                        Log.e(TAG, "Name: " + taskSnapshot.metadata!!.name)
                        tvFileLoad.text = taskSnapshot.metadata!!.path + " - " + taskSnapshot.metadata!!.sizeBytes / 1024 + " KBs"

                        writeNewImageInfoToDB(name!!, url)

                        Toast.makeText(this, "File Uploaded ", Toast.LENGTH_LONG).show()
                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(this, exception.message, Toast.LENGTH_LONG).show()
                    }
                    .addOnProgressListener { taskSnapshot ->
                        // progress percentage
                        val progress = 100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount

                        // percentage in progress
                        val intProgress = progress.toInt()
                        tvFileLoad.text = "Uploaded " + intProgress + "%..."
                    }
                    .addOnPausedListener { System.out.println("Upload is paused!") }

        } else {
            Toast.makeText(this, "No File!", Toast.LENGTH_LONG).show()
        }
    }

    private fun writeNewImageInfoToDB(name: String, url: String) {
        val info = UploadInfo(name, url)

        val key = dataReference!!.push().key
        dataReference!!.child(key).setValue(info)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CHOOSING_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            fileUri = data.data
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        mAdapter!!.cleanup()
    }

    private fun showChoosingFile() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), CHOOSING_IMAGE_REQUEST)
    }

    private fun getFileExtension(uri: Uri): String {
        val contentResolver = contentResolver
        val mime = MimeTypeMap.getSingleton()

        return mime.getExtensionFromMimeType(contentResolver.getType(uri))
    }

    private fun validateInputFileName(fileName: String): Boolean {
        if (TextUtils.isEmpty(fileName)) {
            Toast.makeText(this, "Enter file name!", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }
}