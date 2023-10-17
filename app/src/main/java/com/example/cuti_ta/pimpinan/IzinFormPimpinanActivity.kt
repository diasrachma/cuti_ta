package com.example.cuti_ta.pimpinan

import android.app.DatePickerDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_izin_form.*
import org.json.JSONObject
import java.util.*

class IzinFormPimpinanActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    var tahun = 0
    var bulan = 0
    var hari = 0
    var jam = 0
    var menit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_izin_form)
        supportActionBar?.setTitle("Form Izin Keluar")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        val cal : Calendar = Calendar.getInstance()

        bulan = cal.get(Calendar.MONTH)+1
        hari = cal.get(Calendar.DAY_OF_MONTH)
        tahun = cal.get(Calendar.YEAR)
        jam = cal.get(Calendar.HOUR_OF_DAY)
        menit = cal.get(Calendar.MINUTE)

        btnTglIzin.setOnClickListener {
            showDialog(10)
        }

        btnJamIzin.setOnClickListener {
            showDialog(20)
        }

        btnJamKembali.setOnClickListener {
            showDialog(30)
        }

        btnSimpanFormIzin.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim Izin")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah anda yakin ingin mengirim form izin keluar?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    addDataIzin("insert")
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        btnCancelFormIzin.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Batal Cuti")
                .setMessage("Apakah anda yakin ingin membatalkan?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
            true
        }
    }

    private fun addDataIzin(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if (respon.equals("1")) {
                    Toast.makeText(this, "Berhasil mengirim form izin keluar kantor", Toast.LENGTH_LONG)
                        .show()
                } else {
                    Toast.makeText(this, "Gagal mengirim form izin keluar kantor", Toast.LENGTH_LONG)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                when(mode){
                    "insert"->{
                        hm.put("mode","insert")
                        hm.put("nip", tvNipIzin.text.toString())
                        hm.put("tgl_izinkeluar", txTglIzin.text.toString())
                        hm.put("jam_izinkeluar", txJamIzin.text.toString())
                        hm.put("jam_kembali", txJamKembali.text.toString())
                        hm.put("alasan_izinkeluar", txAlasanIzin.text.toString())
                        hm.put("alamat_izinkeluar", txAlamatIzin.text.toString())
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    var timeChangeDialog = TimePickerDialog.OnTimeSetListener{ view, hourOfDay, minute ->
        txJamIzin.setText("$hourOfDay:$minute")
        jam = hourOfDay
        menit = minute
    }

    var kembaliChangeDialog = TimePickerDialog.OnTimeSetListener{ view, hourOfDay, minute ->
        txJamKembali.setText("$hourOfDay:$minute")
        jam = hourOfDay
        menit = minute
    }

    var ubahTanggalDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        txTglIzin.text = "$year-${month+1}-$dayOfMonth"
        tahun = year
        bulan = month+1
        hari = dayOfMonth
    }

    override fun onCreateDialog(id: Int): Dialog {
        when(id){
            10 -> return DatePickerDialog(this, ubahTanggalDialog, tahun, bulan-1, hari)
            20 -> return TimePickerDialog(this, timeChangeDialog,jam,menit, true)
            30 -> return TimePickerDialog(this, kembaliChangeDialog,jam,menit, true)
        }
        return super.onCreateDialog(id)
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

                tvNamaIzin.setText(nama)
                tvNipIzin.setText(nip)
                tvJabatanIzin.setText(jabatan)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
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