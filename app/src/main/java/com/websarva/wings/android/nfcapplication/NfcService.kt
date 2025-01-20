package com.websarva.wings.android.nfcapplication

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.util.Log
import jp.co.osstech.libjeid.DriverLicenseAP
import jp.co.osstech.libjeid.InvalidPinException
import jp.co.osstech.libjeid.JeidReader
import jp.co.osstech.libjeid.dl.DriverLicenseCommonData
import jp.co.osstech.libjeid.dl.DriverLicenseEntries
import jp.co.osstech.libjeid.dl.DriverLicenseFiles
import jp.co.osstech.libjeid.dl.DriverLicensePhoto
import java.io.IOException

class NfcService(private val context: Context, private val activity : Activity) {

    private var nfcmanager: NfcManager? = null
    private var nfcadapter: NfcAdapter? = null
    private var callback: CustomReaderCallback? = null

    fun scanStart() {
        callback = CustomReaderCallback()
        nfcmanager = context.getSystemService(Context.NFC_SERVICE) as? NfcManager
        nfcadapter = nfcmanager!!.defaultAdapter
        nfcadapter!!.enableReaderMode(activity, callback, NfcAdapter.FLAG_READER_NFC_B ,null)
    }

    fun scanStop() {
        nfcadapter!!.disableReaderMode(activity)
        callback = null
    }

    private class CustomReaderCallback : NfcAdapter.ReaderCallback {
        private fun drive(reader:JeidReader){
            try {
                val ap: DriverLicenseAP = reader.selectDriverLicenseAP()
                // 暗証番号1を入力
                try {
                    ap.verifyPin1("XXXX")
                } catch (e: InvalidPinException) {
                    if (e.isBlocked()) {
                        println("暗証番号1がブロックされています。")
                    } else {
                        System.out.println("暗証番号1が間違っています。残り回数: " + e.getCounter())
                    }
                    return
                }

                // 暗証番号2を入力
                try {
                    ap.verifyPin2("XXXX")
                } catch (e: InvalidPinException) {
                    if (e.isBlocked()) {
                        println("暗証番号2がブロックされています。")
                    } else {
                        System.out.println("暗証番号2が間違っています。残り回数: " + e.getCounter())
                    }
                    return
                }

                // Filesオブジェクトの読み出し
                val files: DriverLicenseFiles = ap.readFiles()

                // Filesオブジェクトから共通データ要素を取得
                val commonData: DriverLicenseCommonData = files.getCommonData()
                System.out.println(commonData.toString())

                // Filesオブジェクトから券面情報を取得
                val entries: DriverLicenseEntries = files.getEntries()
                System.out.println(entries.toString())

                // Filesオブジェクトから顔写真を取得
                val photo: DriverLicensePhoto = files.getPhoto()
            } catch (e: IOException) {
                println("読み取りエラー")
            }
        }

        // NFCタグが検出されると呼ばれる
        override fun onTagDiscovered(tag: Tag) {
            Log.i("NFC Log", "タグを検出したよ")
            Log.i("NFC Log", "タグのID：" + tag.id.toString())
            val reader = JeidReader(tag)

        }
    }
}