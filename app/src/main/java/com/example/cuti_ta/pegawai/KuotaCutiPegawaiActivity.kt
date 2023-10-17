package com.example.cuti_ta.pegawai

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_kuota_detail.*
import org.json.JSONObject
import java.util.HashMap

class KuotaCutiPegawaiActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kuota_detail)
        supportActionBar?.setTitle("Kuota Cuti Anda")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showKuotaCuti("readDetailKuota")
    }

    private fun showKuotaCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlSisa,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val tahunSisa = jsonObject.getString("tahun_kuota")
                val sisaCuti = jsonObject.getString("sisa_kuota")
                val terpakai = jsonObject.getString("terpakai")

                tvTerpakaiKuotaDetail.setText(terpakai + " Hari")
                tvNamaKuotaDetail.setText(nama)
                tvTahunKuotaDetail.setText(tahunSisa)
                tvCutiKuotaDetail.setText(sisaCuti + " Hari")
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                when(mode){
                    "readDetailKuota" -> {
                        hm.put("mode","readDetailKuota")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}