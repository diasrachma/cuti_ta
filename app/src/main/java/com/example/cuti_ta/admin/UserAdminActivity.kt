package com.example.cuti_ta.admin

import android.os.Bundle
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
import com.example.cuti_ta.adapter.AdapterUser
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.activity_user.*
import org.json.JSONArray

class UserAdminActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val daftarUser = mutableListOf<HashMap<String, String>>()
    lateinit var userAdapter: AdapterUser

    var jabatanPimpinan = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user)
        supportActionBar?.setTitle("Master User")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        jabatanPimpinan = paket?.getString("jabatan").toString()

        userAdapter = AdapterUser(daftarUser, this)
        rvUser.layoutManager = LinearLayoutManager(this)
        rvUser.adapter = userAdapter

        btnSearchUser.setOnClickListener {
            showAllUser("readAllUser", textSearchUser.text.toString().trim())
        }
    }

    override fun onStart() {
        super.onStart()
        showAllUser("readAllUser", "")
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun showAllUser(mode: String, nama : String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlUser,
            Response.Listener { response ->
                daftarUser.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("jabatan",jsonObject.getString("jabatan"))
                        frm.put("sts_akun",jsonObject.getString("sts_akun"))
                        frm.put("nip",jsonObject.getString("nip"))
                        frm.put("foto",jsonObject.getString("foto"))

                        daftarUser.add(frm)
                    }
                    userAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("nama",nama)
                when(mode){
                    "readAllUser" -> {
                        hm.put("mode","readAllUser")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}