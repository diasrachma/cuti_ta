package com.example.cuti_ta.admin

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_pn.Helper.MediaHelper
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_ttd_insert.*
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap

class TtdInsertAdminActiviity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    lateinit var mediaHealper: MediaHelper
    var imStr = ""
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ttd_insert)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()
        mediaHealper = MediaHelper(this)

        btnInsImageTtd.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaHealper.RcGallery())
        }
        
        btnSimpanInsertTtd.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim?")
                .setIcon(R.drawable.warning)
                .setMessage("Jika Anda menekan tombol Next maka Akun akan Otomatis Terdaftar, Ingin mendaftarkan Akun?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    insertTtd("insertTtd")
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHealper.RcGallery()){
                imStr = mediaHealper.getBitmapToString(data!!.data,insImageTtd)
            }
        }
    }

    private fun insertTtd(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                if(respon.equals("0")){
                    Toast.makeText(this,"Gagal menambahkan Tanda Tangan", Toast.LENGTH_LONG).show()
                }else {
                    Toast.makeText(this,"Berhasil menambahkan Tanda Tangan", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                val nmFile ="TTD-"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(
                    Date()
                )+".jpg"
                when(mode){
                    "insertTtd" -> {
                        hm.put("mode","insertTtd")
                        hm.put("nip",insNipTtd.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }


}