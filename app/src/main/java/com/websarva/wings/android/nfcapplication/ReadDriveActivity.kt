package com.websarva.wings.android.nfcapplication

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.io.IOException

class ReadDriveActivity : AppCompatActivity() {
    private lateinit var nfcAdapter:NfcAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_drive)

        val pin1 = findViewById<EditText>(R.id.editTextPIN1)
        val pin2 = findViewById<EditText>(R.id.editTextPIN2)
        val errorPIN1 =findViewById<TextView>(R.id.errorPIN1)
        val errorPIN2 = findViewById<TextView>(R.id.errorPIN2)
        val buttonReadDrive = findViewById<Button>(R.id.buttonReadDrive)

        nfcAdapter = NfcAdapter.getDefaultAdapter(this)

        pin1.setOnFocusChangeListener { _, boolHasFocus ->
            if (boolHasFocus) {
                return@setOnFocusChangeListener
            }
            if(pin1.text.toString() == ""){
                errorPIN1.text = "未入力"
                return@setOnFocusChangeListener
            }
        }

        pin2.setOnFocusChangeListener { _, boolHasFocus ->
            if (boolHasFocus) {
                return@setOnFocusChangeListener
            }
            if(pin2.text.toString() == ""){
                errorPIN2.text ="未入力"
                return@setOnFocusChangeListener
            }
        }

        buttonReadDrive.setOnClickListener{

        }
    }

    override fun onResume() {
        super.onResume()
        val intent = Intent(this,javaClass).apply {
            addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(this,0,intent,0)
        val filters = arrayOf(IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED))
        val techList = arrayOf(arrayOf(IsoDep::class.java.name))
        nfcAdapter.enableForegroundDispatch(this,pendingIntent,filters,techList)
    }

    override fun onPause() {
        super.onPause()
        nfcAdapter.disableForegroundDispatch(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        if(intent != null && NfcAdapter.ACTION_TECH_DISCOVERED == intent.action){
            val tag = intent.getParcelableExtra<Tag>(NfcAdapter.EXTRA_TAG)
            tag?.let{
                val isoDep = IsoDep.get(tag)
                isoDep?.let{
                    try{
                        isoDep.connect()
                        readDG1Data(isoDep)
                    }catch (e:IOException){
                        e.printStackTrace()
                    }finally {
                        isoDep.close()
                    }
                }
            }
        }
    }

    private fun readDG1Data(isoDep: IsoDep){
        val selectAIDCmd = byteArrayOf(0x00.toByte(),0xA4.toByte(),0x04.toByte(),0x0C.toByte(),0x07.toByte(),0xA0.toByte(),0,0,0x02.toByte(),0x47.toByte(),0x10.toByte(),0x01.toByte())
        val selectDG1Cmd = byteArrayOf(0,0xA4.toByte(),0x02.toByte(),0x0C.toByte(),0x02.toByte(),0x01.toByte(),0x01.toByte())
    }
}