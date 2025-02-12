package com.example.securevote

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var btnCaptureImage: Button
    private lateinit var btnVoteNow: Button
    private lateinit var ivCapturedImage: ImageView

    private var imageCaptured = false

    private val cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val bitmap = data.extras?.get("data") as? Bitmap
                if (bitmap != null) {

                    if (detectFace(bitmap)) {
                        ivCapturedImage.setImageBitmap(bitmap)
                        imageCaptured = true
                        Toast.makeText(this, "Face detected. Please click Vote Now button.", Toast.LENGTH_LONG).show()
                    } else {
                        imageCaptured = false
                        Toast.makeText(this, "No face detected. Please try again.", Toast.LENGTH_LONG).show()
                    }
                } else {
                    Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Image capture cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        btnCaptureImage = findViewById(R.id.btnCaptureImage)
        btnVoteNow = findViewById(R.id.btnVoteNow)
        ivCapturedImage = findViewById(R.id.ivCapturedImage)

        btnCaptureImage.setOnClickListener {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
            cameraIntent.putExtra("android.intent.extras.LENS_FACING_FRONT", 1)
            cameraLauncher.launch(cameraIntent)
        }

        btnVoteNow.setOnClickListener {
            if (imageCaptured) {
                val intent = Intent(this, VoteActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Image not captured or no face detected", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    /**
//     * Stub function for face detection.
//     * Replace this with your actual face detection logic (e.g., using ML Kit Face Detection).
//     *
//     * @param bitmap The captured image.
//     * @return True if a face is detected, false otherwise.
//     */
    private fun detectFace(bitmap: Bitmap): Boolean {
        return true
    }
}
