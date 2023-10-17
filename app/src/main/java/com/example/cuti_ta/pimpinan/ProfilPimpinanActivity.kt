package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_profil.*
import org.json.JSONObject
import java.util.HashMap

class ProfilPimpinanActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    var nipP = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profil)
        supportActionBar?.setTitle("Profil Akun")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        btnEditProfilMenu.setOnClickListener {
            val intent = Intent(this, ProfilEditPimpinanActivity::class.java)
            intent.putExtra("nip", nipP)
            startActivity(intent)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onStart() {
        super.onStart()
        showProfil("show_profil")
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlProfil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val unitKerja = jsonObject.getString("unit_kerja")
                val masaKerja = jsonObject.getString("masa_kerja")
                val noHp = jsonObject.getString("no_hp")
                val user = jsonObject.getString("level")
                val foto = jsonObject.getString("foto")

                if (unitKerja.equals("Sekretaris")){
                    unitkerjaShowProfil.setText("Kesekretariatan")
                } else {
                    unitkerjaShowProfil.setText("Kepaniteraan")
                }
                nipP = nip

                namaShowProfil.setText(nama)
                nipShowProfil.setText(nip)
                jabatanShowProfil.setText(jabatan)
                golonganShowProfil.setText(golongan)
                masakerjaShowProfil.setText(masaKerja)
                nohpShowProfil.setText(noHp)
                levelShowProfil.setText(user)
                Picasso.get().load(foto).into(imageShowProfil)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "show_profil" -> {
                        hm.put("mode","show_profil")
                        hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                        hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}