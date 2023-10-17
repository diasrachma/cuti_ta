package com.example.cuti_ta.admin

import android.content.DialogInterface
import android.content.Intent
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
import com.example.cuti_ta.pimpinan.ValidasiTangguhkanFragment
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_cuti_validasi.*
import org.json.JSONObject
import java.util.*
import kotlin.collections.HashMap

class CutiValidasiAdminActivity : AppCompatActivity() {
    val cal : Calendar = Calendar.getInstance()

    var tahun = 0
    var bulan = 0
    var hari = 0

    lateinit var urlClass: UrlClass

    var idTtd = ""
    var nipPegawai = ""

    var usr = ""
    var pw = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cuti_validasi)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        var paket : Bundle? = intent.extras
        tvIdCutiValidasi.setText(paket?.getString("idCuti").toString())
        Toast.makeText(this, "jabatan : "+paket?.getString("jbt").toString(), Toast.LENGTH_SHORT).show()
        tvJabatanValidasi.setText(paket?.getString("jbt").toString())
        tvJenis.setText(paket?.getString("jenis").toString())
        tvLamaCutiValidasi.setText(paket?.getString("lama").toString() + " hari")
        nipPegawai = paket?.getString("nip").toString()
        usr = paket?.getString("username").toString()
        pw = paket?.getString("password").toString()

        if (tvJabatanValidasi.text.toString().equals("Ketua")) {
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

        val intent = Intent(this@CutiValidasiAdminActivity, ArsipAdminActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)

        btnTerima.setOnClickListener {
            AlertDialog.Builder(this)
                .setIcon(R.drawable.warning)
                .setTitle("Informasi!")
                .setMessage("Apakah Anda yakin ingin melakukan validasi cuti sebagai "+tvJabatanValidasi.text.toString()+"?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (tvJabatanValidasi.text.toString().equals("Ketua")) {
                        if (tvJenis.text.toString().equals("Cuti Tahunan")) {
                            ttdKetua("ttd_cuti_ketua_tahunan")
                        } else {
                            ttdKetua("ttd_cuti_ketua")
                        }
                        startActivity(intent)
                    } else {
                        ttdPimpinan("ttd_cuti_pimpinan")
                        startActivity(intent)
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
                        hm.put("username", usr)
                        hm.put("password", pw)
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
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "ttd_cuti_pimpinan" -> {
                        hm.put("mode","ttd_cuti_pimpinan")
                        hm.put("id_cuti", tvIdCutiValidasi.text.toString())
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
                        hm.put("id_cuti", tvIdCutiValidasi.text.toString())
                        hm.put("ttd_ketua", idTtd)
                        hm.put("ttd_tglketua", tvTanggalValidasi.text.toString())
                    }
                    "ttd_cuti_ketua_tahunan" -> {
                        hm.put("mode","ttd_cuti_ketua_tahunan")
                        hm.put("id_cuti", tvIdCutiValidasi.text.toString())
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