package com.absensi.langitpay.absen.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import com.absensi.langitpay.R
import com.absensi.langitpay.abstraction.loaderDialog
import com.absensi.langitpay.abstraction.toast
import com.absensi.langitpay.abstraction.visibilism
import com.absensi.langitpay.abstraction.withPermissions
import com.bumptech.glide.Glide
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.size.SizeSelector
import com.otaliastudios.cameraview.size.SizeSelectors
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import id.zelory.compressor.Compressor
import kotlinx.android.synthetic.main.activity_camera.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class CameraActivity : AppCompatActivity() {

    private val loader by lazy {
        loaderDialog()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
        camera.setLifecycleOwner(this)

        camera.setPictureSize(setSizeCamera())
        camera.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(result: PictureResult) {
                super.onPictureTaken(result)

                loader.dismiss()
                val permissions = listOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
                withPermissions(permissions,action = { _, deniedList ->
                    if (deniedList.isEmpty()){

                        val file = File.createTempFile("LangitPay", ".png")
                        result.toFile(file) {
                            Log.i("Image",file.absolutePath)
                            launchImageCrop(Uri.fromFile(it))
                        }
                    }
                })
            }
        })

        btn_take_picture.setOnClickListener {
            visibility(
                cameraShow = true,
                linButtonShow = false,
                previewShow = false,
                btnTakePicture = true
            ) {
                camera.takePicture()
                loader.show()
            }
        }
        btn_try_again.setOnClickListener {
            visibility(
                cameraShow = true,
                linButtonShow = false,
                previewShow = false,
                btnTakePicture = true
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            val result = CropImage.getActivityResult(data)
            visibility(
                cameraShow = false,
                linButtonShow = true,
                previewShow = true,
                btnTakePicture = false
            ) {
                Glide.with(this).load(result.uri).into(image_preview)
                if (result != null){
                    GlobalScope.launch {
                        Compressor.compress(this@CameraActivity,result.uri.toFile()).apply {
                            btn_upload.setOnClickListener {
                                loader.show()
                                Handler().postDelayed({
                                    loader.dismiss()
                                    val intent = Intent()
                                    intent.putExtra("image", result.uri.toFile().absolutePath)
                                    setResult(Activity.RESULT_OK, intent)
                                    finish()
                                },500)
                            }
                        }
                    }
                }else{
                    btn_upload.isEnabled = false
                    btn_upload.setBackgroundResource(R.drawable.bg_button_unactive)
                    toast("Terjadi Kesalahan Silahkan Ambil Photo Lagi")
                }
            }
        }
    }

    private fun launchImageCrop(uri: Uri){
        CropImage.activity(uri)
            .setGuidelines(CropImageView.Guidelines.ON)
            .start(this)
    }

    private fun visibility(
        cameraShow: Boolean,
        linButtonShow: Boolean,
        previewShow: Boolean,
        btnTakePicture: Boolean,
        body: (() -> Unit)? = null
    ) {
        camera.visibilism(cameraShow)
        lin_button.visibilism(linButtonShow)
        image_preview.visibilism(previewShow)
        btn_take_picture.visibilism(btnTakePicture)
        Handler().postDelayed({
            body?.invoke()
        }, 200)
    }

    override fun onResume() {
        super.onResume()
        camera.open()
    }

    override fun onPause() {
        super.onPause()
        camera.close()
    }

    override fun onDestroy() {
        super.onDestroy()
        camera.destroy()
    }

    private fun  setSizeCamera(): SizeSelector{
        return SizeSelectors.maxWidth(600)
    }
}
