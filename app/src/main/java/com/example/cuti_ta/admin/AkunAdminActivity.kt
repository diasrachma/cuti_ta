package com.example.cuti_ta.admin

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterAkun
import kotlinx.android.synthetic.main.activity_akun.*
import kotlinx.android.synthetic.main.activity_ttd.*
import org.json.JSONArray

class AkunAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarAkun = mutableListOf<HashMap<String, String>>()
    lateinit var akunAdapter: AdapterAkun

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun)
        supportActionBar?.setTitle("Master Akun")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        akunAdapter = AdapterAkun(daftarAkun, this)
        rvAkun.layoutManager = LinearLayoutManager(this)
        rvAkun.adapter = akunAdapter

        btnInsertAkun.setOnClickListener {
            val intent = Intent(this, AkunInsertAdminActivity::class.java)
            startActivity(intent)
        }

        btnSearchAkunAdmin.setOnClickListener {
            showAllAkun("readAllAkun", textSearchArsipAdmin.text.toString().trim())
        }
    }

    override fun onStart() {
        super.onStart()
        showAllAkun("readAllAkun", "")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showAllAkun(mode: String, username: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                daftarAkun.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("username",jsonObject.getString("username"))
                        frm.put("password",jsonObject.getString("password"))
                        frm.put("level",jsonObject.getString("level"))
                        frm.put("sts_akun",jsonObject.getString("sts_akun"))

                        daftarAkun.add(frm)
                    }
                    akunAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readAllAkun" -> {
                        hm.put("mode","readAllAkun")
                        hm.put("username", username)
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}