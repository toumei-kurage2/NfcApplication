package com.websarva.wings.android.nfcapplication

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.gemalto.jp2.JP2Decoder

class DriveInfoCheckActivity : AppCompatActivity() {
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drive_info_check)
        val name = findViewById<TextView>(R.id.name)
        val birth = findViewById<TextView>(R.id.birth)
        val faceImage = findViewById<ImageView>(R.id.face)
        val buttonTop = findViewById<Button>(R.id.buttonTop)

        name.text = "名前：${intent.getStringExtra("name")}"
        birth.text = "生年月日：${intent.getStringExtra("birth")}"
        val face: ByteArray? = intent.getByteArrayExtra("face")

        faceImage.setImageBitmap(JP2Decoder(face).decode())

        buttonTop.setOnClickListener{
            val intent = Intent(this,SelectCardActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}