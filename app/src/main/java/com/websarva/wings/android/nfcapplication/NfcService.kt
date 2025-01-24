package com.websarva.wings.android.nfcapplication

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.nfc.NfcAdapter
import android.nfc.NfcManager
import android.nfc.Tag
import android.nfc.tech.IsoDep
import android.os.Handler
import android.widget.TextView
import android.widget.Toast
import jp.co.osstech.libjeid.CardType
import jp.co.osstech.libjeid.DriverLicenseAP
import jp.co.osstech.libjeid.InvalidPinException
import jp.co.osstech.libjeid.JeidReader
import jp.co.osstech.libjeid.dl.DriverLicenseFiles
import java.io.IOException
import java.text.SimpleDateFormat

class NfcService(private val context: Context, private val activity : Activity,private val keyList:ArrayList<String>,private val scanMessage:TextView) {

    private var nfcmanager: NfcManager? = null
    private var nfcAdapter = NfcAdapter.getDefaultAdapter(context)
    private var callback: CustomReaderCallback? = null

    fun scanStart() {
        callback = CustomReaderCallback(context,keyList,scanMessage)
        nfcmanager = context.getSystemService(Context.NFC_SERVICE) as? NfcManager
        nfcAdapter!!.enableReaderMode(activity, callback, NfcAdapter.FLAG_READER_NFC_B ,null)
    }

    fun scanStop() {
        scanMessage.text ="読み取り不可"
        nfcAdapter!!.disableReaderMode(activity)
        callback = null
    }

    private class CustomReaderCallback(private val context: Context,private val keyList: ArrayList<String>,private val scanMessage: TextView) : NfcAdapter.ReaderCallback {
        //非同期処理制御
        private val handler: Handler = Handler()
        @SuppressLint("SimpleDateFormat")
        private fun drive(reader:JeidReader,isoDep:IsoDep){
            try {
                val ap: DriverLicenseAP = reader.selectDriverLicenseAP()
                // 暗証番号1を入力
                try {
                    ap.verifyPin1(keyList[0])
                } catch (e: InvalidPinException) {
                    if (e.isBlocked) {
                        throw Exception("暗証番号1がブロックされています。")
                    } else {
                         throw Exception("暗証番号1が間違っています。残り回数: ${e.counter}")
                    }
                }

                // 暗証番号2を入力
                try {
                    ap.verifyPin2(keyList[1])
                } catch (e: InvalidPinException) {
                    if (e.isBlocked) {
                        throw Exception("暗証番号2がブロックされています。")
                    } else {
                        throw Exception("暗証番号2が間違っています。残り回数: ${e.counter}")
                    }
                }

                // Filesオブジェクトの読み出し
                val files: DriverLicenseFiles = ap.readFiles()

                // Filesオブジェクトから券面情報を取得
                val birthDate = files.entries.birthDate.toDate()
                val format = SimpleDateFormat("yyyy/MM/dd")
                val birth = format.format(birthDate)
                val name = files.entries.name.toString()
                // Filesオブジェクトから顔写真を取得
                val face= files.photo.photo
                val intent = Intent(context,DriveInfoCheckActivity::class.java)
                intent.putExtra("name",name)
                intent.putExtra("birth",birth)
                intent.putExtra("face",face)
                context.startActivity(intent)
            } catch (e: IOException) {
                throw Exception("カードの接続が途切れました。")
            } catch (e: Exception) {
                throw Exception("予期せぬエラーが発生しました。")
            }
            finally {
                isoDep.close()
            }
        }

        // NFCタグが検出されると呼ばれる
        override fun onTagDiscovered(tag: Tag) {
            val thread = Thread{
                scanMessage.text = "スキャン中"
            }
            thread.start()
            val isoDep = IsoDep.get(tag)
            val reader = JeidReader(tag)
            try {
                //カード種別の判断
                val type = reader.detectCardType()
                if (type == CardType.DL) {
                    drive(reader,isoDep)
                }
            }catch (e:Exception){
                handler.post{
                    Toast.makeText(context,e.message.toString(),Toast.LENGTH_SHORT).show()
                    scanMessage.text = "スキャン準備中"
                }
            }finally {
                isoDep.close()
            }
        }
    }
}