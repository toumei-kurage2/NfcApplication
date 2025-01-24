package com.websarva.wings.android.nfcapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SelectCardActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_card)

        val buttonDrive = findViewById<Button>(R.id.buttonDrive)
        val buttonMyna = findViewById<Button>(R.id.buttonMyna)

        buttonDrive.setOnClickListener{
            val intent = Intent(applicationContext, ReadDriveActivity::class.java)
            startActivity(intent)
            this.finish()
        }

        buttonMyna.setOnClickListener{
            val intent = Intent(applicationContext,ReadMynaActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }
}