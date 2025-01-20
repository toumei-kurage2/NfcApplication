package com.websarva.wings.android.nfcapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class SelectCardActivity : AppCompatActivity() {
    private var nfcService = NfcService(this,this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_card)

        val buttonDrive = findViewById<Button>(R.id.buttonDrive)

        buttonDrive.setOnClickListener{
            val intent = Intent(applicationContext, ReadDriveActivity::class.java)
            startActivity(intent)
            this.finish()
        }
    }

    override fun onResume() {
        super.onResume()
        nfcService.scanStart()
    }

    override fun onPause() {
        super.onPause()
        nfcService.scanStop()
    }
}