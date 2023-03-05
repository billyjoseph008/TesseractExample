package com.brainstormideas.tesseract_example

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.brainstormideas.tesseract_example.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

    private val STORAGE_PERMISSION = Manifest.permission.WRITE_EXTERNAL_STORAGE

    private val imageChose = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK)
            binding.ivCamera.setImageURI(it.data?.data)
    }

    private lateinit var readImageText : ReadImageText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnAdd.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            imageChose.launch(intent)
        }

        binding.btnProcess.setOnClickListener {
            if (binding.ivCamera.drawable != null){
                lifecycleScope.launch {
                    val bitmapDrawable : BitmapDrawable = binding.ivCamera.drawable as BitmapDrawable
                    binding.txtImageText.text = readImageText.processImage(bitmapDrawable.bitmap, "spa")
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        val permissionCheck = ContextCompat.checkSelfPermission(
            applicationContext, STORAGE_PERMISSION
        )

        if (permissionCheck != PackageManager.PERMISSION_GRANTED){
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, STORAGE_PERMISSION)){
                ActivityCompat.requestPermissions(this, arrayOf(STORAGE_PERMISSION), 0)
            }else{
                val intent = Intent()
                intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                intent.data = Uri.fromParts( "package", this.packageName, null )
                this.startActivity(intent)
            }
        }else {
            readImageText = ReadImageText(applicationContext)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        readImageText.recycle()
    }
}