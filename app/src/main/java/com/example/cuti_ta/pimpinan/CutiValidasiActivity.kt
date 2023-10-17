package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cuti_validasi.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class CutiValidasiActivity : AppCompatActivity() {

    val cal : Calendar = Calendar.getInstance()

    var tahun = 0
    var bulan = 0
    var hari = 0

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""
    val JABATAN = "jabatan"
    val DEF_JABATAN = ""

    var idTtd = ""
    var nipPegawai = ""
    var jbt_pegawai = ""
    var idCuti = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti_validasi)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        var paket : Bundle? = intent.extras
        idCuti = paket?.getString("idCuti").toString()
        tvIdCutiValidasi.setText(paket?.getString("idCuti").toString())
        Toast.makeText(this, "jabatan : "+preferences.getString(JABATAN, DEF_JABATAN).toString(), Toast.LENGTH_SHORT).show()
        tvJabatanValidasi.setText(preferences.getString(JABATAN, DEF_JABATAN).toString())
        tvLamaCutiValidasi.setText(paket?.getString("lama").toString() + " hari")
        nipPegawai = paket?.getString("nip").toString()

        if (tvJabatanValidasi.text.toString().equals("Ketua") || tvJabatanValidasi.text.toString().equals("Wakil Ketua")) {
            textInputLayout3.visibility = View.GONE
        } else {
            textInputLayout3.visibility = View.VISIBLE
        }

        btnTangguhkan.setOnClickListener {
            var dialog = ValidasiTangguhkanFragment()

            val bundle = Bundle().apply {
                putString("idCuti", tvIdCutiValidasi.text.toString())
            }
            dialog.arguments = bundle

            dialog.show(this.supportFragmentManager, "ValidasiTangguhkanFragment")
        }

        btnTerima.setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("Informasi!")
                .setMessage("Apakah Anda yakin ingin melakukan validasi cuti sebagai "+tvJabatanValidasi.text.toString()+"?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (tvJabatanValidasi.text.toString().equals("Ketua") || tvJabatanValidasi.text.toString().equals("Wakil Ketua")) {
                        if (jbt_pegawai.equals("Hakim") && tvJabatanValidasi.text.toString().equals("Wakil Ketua")) {
                            ttdPimpinan("ttd_cuti_pimpinan")
                        } else {
                            if (tvJenis.text.toString().equals("Cuti Tahunan")) {
                                ttdKetua("ttd_cuti_ketua_tahunan")
                            } else {
                                ttdKetua("ttd_cuti_ketua")
                            }
                        }
                    } else {
                        ttdPimpinan("ttd_cuti_pimpinan")
                    }
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                    onBackPressed()
                })
                .show()
            true
        }

        bulan = cal.get(Calendar.MONTH)+1
        hari = cal.get(Calendar.DAY_OF_MONTH)
        tahun = cal.get(Calendar.YEAR)

        tvTanggalValidasi.setText("$tahun-$bulan-$hari")
    }

    override fun onStart() {
        super.onStart()
        myTT("readTtdPimpinan")
        showDetailCuti("detail_admin")
    }

    fun showDetailCuti(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jenis = jsonObject.getString("jenis_cuti")
                val jabatan = jsonObject.getString("jabatan")

                jbt_pegawai = jabatan
                tvJenis.setText(jenis)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this, "Tidak dapat terhubung ke server!", Toast.LENGTH_SHORT).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "detail_admin" -> {
                        hm.put("mode","detail_admin")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun myTT(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlTtd,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val id = jsonObject.getString("id_ttd")
                val nama = jsonObject.getString("nama")
                val nip = jsonObject.getString("nip")
                val foto = jsonObject.getString("img_ttd")

                idTtd  = id
                txNamaValidasi.setText(nama)
                txNipValidasi.setText(nip)
                Picasso.get().load(foto).into(imgTtd1Validasi)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "readTtdPimpinan" -> {
                        hm.put("mode","readTtdPimpinan")
                        hm.put("username", preferences.getString(USERNAME, DEF_USERNAME).toString())
                        hm.put("password", preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun ttdPimpinan(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("0")){
                    Toast.makeText(this,"Gagal Melakukan Validasi", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Barhasil Melakukan Validasi", Toast.LENGTH_LONG).show()
                    recreate()
                    val intent = Intent(this, ArsipDetailPimpinanActivity::class.java)
                    intent.putExtra("idCuti", idCuti)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "ttd_cuti_pimpinan" -> {
                        hm.put("mode","ttd_cuti_pimpinan")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                        hm.put("catatan", txCatatanValidasi.text.toString())
                        hm.put("ttd_pimpinan", idTtd)
                        hm.put("ttd_tglpimpinan", tvTanggalValidasi.text.toString())
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun ttdKetua(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("0")){
                    Toast.makeText(this,"Gagal Melakukan Validasi", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Barhasil Melakukan Validasi", Toast.LENGTH_LONG).show()
                    recreate()
                    val intent = Intent(this, ArsipDetailPimpinanActivity::class.java)
                    intent.putExtra("idCuti", idCuti)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "ttd_cuti_ketua" -> {
                        hm.put("mode","ttd_cuti_ketua")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                        hm.put("ttd_ketua", idTtd)
                        hm.put("ttd_tglketua", tvTanggalValidasi.text.toString())
                    }
                    "ttd_cuti_ketua_tahunan" -> {
                        hm.put("mode","ttd_cuti_ketua_tahunan")
                        hm.put("id_cuti", paket?.getString("idCuti").toString())
                        hm.put("ttd_ketua", idTtd)
                        hm.put("ttd_tglketua", tvTanggalValidasi.text.toString())
                        hm.put("a", paket?.getString("lama").toString())
                        hm.put("nip", nipPegawai)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}