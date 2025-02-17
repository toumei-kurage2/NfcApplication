package com.websarva.wings.android.nfcapplication

import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.IsoDep
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import java.io.IOException
import java.time.chrono.JapaneseDate

class ReadDriveActivity : AppCompatActivity() {

    private var nfcAdapter: NfcAdapter? = null
    private var nfcService :NfcService? = null
    private lateinit var pin1:EditText
    private lateinit var pin2:EditText
    private lateinit var errorPIN1:TextView
    private lateinit var errorPIN2:TextView
    private lateinit var buttonReadDrive:Button
    private lateinit var scanMessage:TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_read_drive)

        pin1 = findViewById<EditText>(R.id.editTextPIN1)
        pin2 = findViewById<EditText>(R.id.editTextPIN2)
        errorPIN1 =findViewById<TextView>(R.id.errorPIN1)
        errorPIN2 = findViewById<TextView>(R.id.errorPIN2)
        buttonReadDrive = findViewById<Button>(R.id.buttonReadDrive)
        scanMessage = findViewById<TextView>(R.id.scanMessage)

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
                errorPIN2.text = "未入力"
                return@setOnFocusChangeListener
            }
        }

        buttonReadDrive.setOnClickListener{
            if(pin1.text.toString() == ""){
                errorPIN1.text = "未入力"
                return@setOnClickListener
            }
            if(pin2.text.toString() == ""){
                errorPIN2.text = "未入力"
                return@setOnClickListener
            }
            clearErrorMessage()
            pin1.clearFocus()
            pin2.clearFocus()
            //キーボード非表示
            val inputMethodManager = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(pin1.windowToken, 0)
            inputMethodManager.hideSoftInputFromWindow(pin2.windowToken,0)
            if(nfcAdapter == null) {
                Toast.makeText(this,"この端末はNFC機能がありません。",Toast.LENGTH_SHORT).show()
            } else if(!nfcAdapter!!.isEnabled) {
                showEnableNFCDialog(this@ReadDriveActivity)
            }else{
                scanMessage.text = "スキャン準備中"
                val keyList = arrayListOf(pin1.text.toString(),pin2.text.toString())
                pin1.isFocusable = false
                pin2.isFocusable = false
                nfcService = NfcService(this,this,keyList,scanMessage)
                nfcService!!.scanStart()
            }
        }
    }

    //NFCの権限がOFFの場合、設定画面から権限をオンにするように促すダイアログ
    private fun showEnableNFCDialog(context: Context?) {
        if (context != null) {
            AlertDialog.Builder(this).setTitle("このアプリはNFCを使用します。NFCを有効にしてください。")
                //左側に「はい」ボタン
                .setNeutralButton("設定を開く") { _, _ ->
                    // NFCの設定画面を開く
                    val intent = Intent(Settings.ACTION_NFC_SETTINGS)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                }
                //右側に「いいえ」ボタン
                .setPositiveButton("キャンセル") { _, _ ->
                    //画面遷移しない
                }.show().setCanceledOnTouchOutside(false)
        }
    }

    override fun onResume() {
        super.onResume()
        if(nfcService != null)
            nfcService!!.scanStart()
    }

    override fun onPause() {
        super.onPause()
        if(nfcService != null)
            nfcService!!.scanStop()
    }

    private fun clearErrorMessage(){
        errorPIN1.text = ""
        errorPIN2.text = ""
    }
}