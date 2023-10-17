package com.example.cuti_ta

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.admin.MainAdminActivity
import com.example.cuti_ta.pegawai.MainActivity
import com.example.cuti_ta.pimpinan.MainPimpinanActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONArray
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    lateinit var preferences: SharedPreferences
    lateinit var urlClass: UrlClass

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""
    val JABATAN = "jabatan"
    val DEF_JABATAN = ""

    lateinit var usernameAdapter: ArrayAdapter<String>
    val daftarUsername = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        usernameAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,daftarUsername)
        etNip.setAdapter(usernameAdapter)
        etNip.threshold = 6
        etNip.setOnItemClickListener { parent, view, position, id ->
            showPassword("lihat_password")
        }

        information.setOnClickListener {
            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Cuti v1.9")
                .setIcon(android.R.drawable.ic_menu_info_details)
                .setMessage("Silahkan masukkan Nomor Handphone sebagai Username dan Password bawaan!")
                .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        btnLogin.setOnClickListener{
            progressBar.visibility = View.VISIBLE
            if (etNip.text.toString().equals("")){
                progressBar.visibility = View.GONE
                Toast.makeText(this,"NIP tidak boleh kosong!", Toast.LENGTH_LONG).show()
            }else if(etPassword.text.toString().equals("")){
                progressBar.visibility = View.GONE
                Toast.makeText(this,"Password tidak boleh kosong!", Toast.LENGTH_LONG).show()
            }else{
                validationAccount("login")
            }
        }
    }

    private fun showPassword(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.validasi,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val password = jsonObject.getString("password")

                etPassword.setText(password)
            },
            Response.ErrorListener { error ->
                AlertDialog.Builder(this)
                    .setTitle("Warning!!")
                    .setIcon(R.drawable.warning)
                    .setMessage("Tidak Bisa Terhubung ke Server Database!")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                hm.put("username", etNip.text.toString())
                when(mode) {
                    "lihat_password" -> {
                        hm.put("mode", "lihat_password")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onStart() {
        super.onStart()
        progressBar.visibility = View.GONE
        getUsername("get_username")
    }

    private fun getUsername(mode: String) {
        val request = object : StringRequest(
            Request.Method.POST,urlClass.validasi,
            Response.Listener { response ->
                daftarUsername.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarUsername.add(jsonObject.getString("username"))
                }
                usernameAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->
                AlertDialog.Builder(this)
                    .setTitle("Warning!!")
                    .setIcon(R.drawable.warning)
                    .setMessage("Tidak Bisa Terhubung ke Server Database!")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->

                    })
                    .show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "get_username" -> {
                        hm.put("mode", "get_username")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun validationAccount(mode: String){
        val request = object : StringRequest(
            Method.POST,urlClass.validasi,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nip = jsonObject.getString("nip")
                val nama = jsonObject.getString("nama")
                val level = jsonObject.getString("level")
                val statusAkun = jsonObject.getString("sts_akun")
                val jabatan = jsonObject.getString("jabatan")
                if(level.equals("Pegawai") && statusAkun.equals("AKTIF")){
                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(USERNAME, etNip.text.toString())
                    prefEditor.putString(PASSWORD, etPassword.text.toString())
                    prefEditor.putString(JABATAN, jabatan)
                    prefEditor.commit()

                    val intent = Intent(this, MainActivity::class.java)
                    intent.putExtra("nama", nama)
                    startActivity(intent)
                    finish()
                }else if(level.equals("Admin") && statusAkun.equals("AKTIF")){
                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(USERNAME,etNip.text.toString())
                    prefEditor.putString(PASSWORD,etPassword.text.toString())
                    prefEditor.putString(JABATAN, jabatan)
                    prefEditor.commit()

                    val intent = Intent(this, MainAdminActivity::class.java)
                    intent.putExtra("nama", nama)
                    startActivity(intent)
                    finish()
                }else if(level.equals("Pimpinan") && statusAkun.equals("AKTIF")) {
                    preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                    val prefEditor = preferences.edit()
                    prefEditor.putString(USERNAME,etNip.text.toString())
                    prefEditor.putString(PASSWORD,etPassword.text.toString())
                    prefEditor.putString(JABATAN, jabatan)
                    prefEditor.commit()

                    val intent = Intent(this, MainPimpinanActivity::class.java)
                    intent.putExtra("nama", nama)
                    startActivity(intent)
                    finish()
                }else if(statusAkun.equals("NON")){
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan!")
                        .setMessage("Status Akun Anda Telah Dinon-aktifkan!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            progressBar.visibility = View.GONE
                        })
                        .show()
                } else{
                    AlertDialog.Builder(this)
                        .setTitle("Peringatan!")
                        .setMessage("Username & Password salah!")
                        .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                            progressBar.visibility = View.GONE
                        })
                        .show()
                }
            },
            Response.ErrorListener { error ->
                AlertDialog.Builder(this)
                    .setTitle("Warning!!")
                    .setIcon(R.drawable.warning)
                    .setMessage("Tidak Bisa Terhubung ke Server Database!")
                    .setPositiveButton("OK", DialogInterface.OnClickListener { dialogInterface, i ->
                        progressBar.visibility = View.GONE
                    })
                    .show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "login" -> {
                        hm.put("mode", "login")
                        hm.put("username",etNip.text.toString())
                        hm.put("password",etPassword.text.toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}