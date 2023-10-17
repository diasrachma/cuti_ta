package com.example.cuti_ta

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.admin.AkunAdminActivity
import kotlinx.android.synthetic.main.activity_privasi_akun.*
import org.json.JSONObject
import java.util.HashMap

class PrivasiAkunActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_privasi_akun)
        supportActionBar?.setTitle("Privasi Akun")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        urlClass = UrlClass()

        btnSimpanEditPrivasi.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim?")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda yakin ingin mengedit username dan password Anda?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    editPrivasi("editPrivasi")
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }
    }

    private fun editPrivasi(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("0")){
                    Toast.makeText(this,"Password lama yang anda masukkan salah", Toast.LENGTH_LONG).show()
                }else if(respon.equals("1")){
                    Toast.makeText(this,"Berhasil mengedit Username dan Password", Toast.LENGTH_LONG).show()
                    val prefEditor = preferences.edit()
                    prefEditor.putString(USERNAME,null)
                    prefEditor.putString(PASSWORD,null)
                    prefEditor.commit()

                    val intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finishAffinity()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "editPrivasi" -> {
                        hm.put("mode","editPrivasi")
                        hm.put("usernameLama", preferences.getString(USERNAME, DEF_USERNAME).toString())
                        hm.put("username", edtUsernamePrivasi.text.toString())
                        hm.put("password", edtPasswordPrivasi.text.toString())
                        hm.put("passwordLama", edtPasswordPrivasiLama.text.toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}