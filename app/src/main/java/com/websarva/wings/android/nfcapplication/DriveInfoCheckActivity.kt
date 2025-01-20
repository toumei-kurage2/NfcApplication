package com.websarva.wings.android.nfcapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.gemalto.jp2.JP2Decoder

class DriveInfoCheckActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_info_check)
        val name = findViewById<TextView>(R.id.name)
        val sex = findViewById<TextView>(R.id.sex)
        val faceImage = findViewById<ImageView>(R.id.face)

        name.text = intent.getStringExtra("name")
        sex.text = intent.getStringExtra("sex")
        val face: ByteArray? = intent.getByteArrayExtra("face")

        faceImage.setImageBitmap(JP2Decoder(face).decode())
    }
}