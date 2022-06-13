package com.gracielo.projectta.ui

import android.app.Activity
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.gracielo.projectta.data.source.remote.network.ApiServices
import com.gracielo.projectta.databinding.ActivityTestUploadImageBinding
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File


class TestUploadImage : AppCompatActivity() {

    private lateinit var binding : ActivityTestUploadImageBinding
    private var imageUri: Uri? = null
    private lateinit var image: ImageView
    private var partImage=""
    val apiServices=ApiServices()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityTestUploadImageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val btnSelect = binding.btnSelect
        val btnUpload= binding.btnUpload
        image = binding.imageView

        var resultLauncher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result->
            if(result.resultCode== RESULT_OK){
                val intentData= result.data
                imageUri=intentData?.data
                image.setImageURI(imageUri)

            }
        }

        btnSelect.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            photoPickerIntent.type = "image/*"
            resultLauncher.launch(photoPickerIntent)

        }
        btnUpload.setOnClickListener {
            if(image.drawable!=null){
                if(imageUri!=null){

//                    val filePath: String? = getRealPathFromURIPath(imageUri!!, this)
//                    val file = File(filePath)
//                    val requestBody: RequestBody =
//                            MultipartBody.Builder().setType(MultipartBody.FORM)
//                                .addFormDataPart("uploaded_file", file.name,file.asRequestBody("images/*".toMediaTypeOrNull()))
//                                .build()
//
////                    val mFile: RequestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
////                        RequestBody.create("image/*".toMediaTypeOrNull(), file) //membungkus file ke dalam request body
//
//                    val body: MultipartBody.Part = MultipartBody.Part.Companion.createFormData(
//                        "file",
//                        file.name,
//                        requestBody
//                    ) // membuat formdata multipart berisi request body
//                    Log.d("File", body.toString())
//                    val function = FormBody.Builder().add("function","uploadPhoto").build()
//
//                    apiServices.uploadPicture(function,body){
//                        Log.d("Upload Image", it!!.string())
//
//                    }

                    Thread{
                        val filePath: String? = getRealPathFromURIPath(imageUri!!, this)
                        val file = File(filePath)
                        val requestBody: RequestBody =
                            MultipartBody.Builder().setType(MultipartBody.FORM)
                                .addFormDataPart("function","uploadPhoto")
                                .addFormDataPart("file", file.name,file.asRequestBody("images/*".toMediaTypeOrNull()))
                                .build()

                        val request: Request = Request.Builder().url("http://192.168.0.109/P10-myservice_php/myservice/master.php/").post(requestBody).build()

                        val response: Response = OkHttpClient().newCall(request).execute()

                        if (response.isSuccessful) {
                            Log.d("File upload",response.body!!.string())
                            Log.d("File upload","success, path: --")
                        } else {
                            Log.e("File upload", "failed")
                        }
                    }.start()
                }



            }
            else{
                Toast.makeText(this,"gambar kosong",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == 100) {
            imageUri = data?.data
            image.setImageURI(imageUri)
        }
    }
    private fun getRealPathFromURIPath(contentURI: Uri, activity: Activity): String? {
        val cursor: Cursor? = activity.contentResolver.query(contentURI, null, null, null, null)
        return if (cursor == null) {
            contentURI.path
        } else {
            cursor.moveToFirst()
            val idx: Int = cursor.getColumnIndex(MediaStore.Images.Media.DATA)
            cursor.getString(idx)

        }
    }
}