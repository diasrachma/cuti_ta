package com.example.cuti_ta.admin

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.apk_pn.Helper.MediaHelper
import com.example.cuti_ta.BaseApplication
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_form.*
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class FormAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass

    var tahun = 0
    var bulan = 0
    var hari = 0

    val jenisSpinner = arrayOf("Cuti Tahunan", "Cuti Besar", "Cuti Sakit", "Cuti Melahirkan", "Cuti Karena Alasan Penting", "Cuti di Luar Tanggungan Negara")
    lateinit var adapterJenis: ArrayAdapter<String>
    var pilihan = ""

    lateinit var namaAdapter: ArrayAdapter<String>
    val daftarNama = mutableListOf<String>()

    lateinit var mediaHealper: MediaHelper
    var imStr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_form)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mediaHealper = MediaHelper(this)
        urlClass = UrlClass()

        tvNamaCuti.setEnabled(true)

        adapterJenis = ArrayAdapter(this, android.R.layout.simple_list_item_1,jenisSpinner)

        namaAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1,daftarNama)
        tvNamaCuti.setAdapter(namaAdapter)
        tvNamaCuti.threshold = 1
        tvNamaCuti.setOnItemClickListener { parent, view, position, id ->
            showProfil("lihat_nama")
        }

        val kalender : Calendar = Calendar.getInstance()

        tahun = kalender.get(Calendar.YEAR)
        bulan = kalender.get(Calendar.MONTH) + 1
        hari = kalender.get(Calendar.DAY_OF_MONTH)

        spJenisCuti.adapter = adapterJenis

        spJenisCuti.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent : AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position == 0) {
                    pilihan = "Cuti Tahunan"
                    boxFotoSakit.visibility = View.GONE
//                    Toast.makeText(baseContext,adapterJenis.getItem(position),Toast.LENGTH_SHORT).show()
                } else {
                    boxFotoSakit.visibility = View.VISIBLE
//                    Toast.makeText(baseContext,adapterJenis.getItem(position),Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnTglCuti.setOnClickListener {
            showDialog(10)
        }

        btnSelesaiCuti.setOnClickListener {
            showDialog(20)
        }

        btnSimpanFormCuti.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim Cuti")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah anda yakin ingin mengirim form permohonan cuti?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    if (spJenisCuti.selectedItem.toString().equals("Cuti Tahunan")) {
                        addDataCuti("insert")
                    } else {
                        addDataCuti("insert_with_photo")
                    }
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        btnChooseFile.setOnClickListener {
            val intent = Intent()
            intent.setType("image/*")
            intent.setAction(Intent.ACTION_GET_CONTENT)
            startActivityForResult(intent,mediaHealper.RcGallery())
        }

        btnCancelFormCuti.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Batal Cuti")
                .setMessage("Apakah anda yakin ingin membatalkan?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    onBackPressed()
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        getNip("get_nama")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == mediaHealper.RcGallery()){
                imStr = mediaHealper.getBitmapToString(data!!.data,imgEvidenceSakit)
            }
        }
    }

    var ubahTanggalDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        tvTanggalCuti.text = "$year-${month+1}-$dayOfMonth"
        tahun = year
        bulan = month+1
        hari = dayOfMonth
    }

    var selesaiCutiDialog = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
        tvSelesaiCuti.text = "$year-${month+1}-$dayOfMonth"
        tahun = year
        bulan = month+1
        hari = dayOfMonth
    }

    override fun onCreateDialog(id: Int): Dialog {
        when(id){
            10 -> return DatePickerDialog(this, ubahTanggalDialog, tahun, bulan-1, hari)
            20 -> return DatePickerDialog(this, selesaiCutiDialog, tahun, bulan-1, hari)
        }
        return super.onCreateDialog(id)
    }

    private fun getNip(mode: String) {
        val request = object : StringRequest(
            Request.Method.POST,urlClass.urlUser,
            Response.Listener { response ->
                daftarNama.clear()
                val jsonArray = JSONArray(response)
                for (x in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(x)
                    daftarNama.add(jsonObject.getString("nama"))
                }
                namaAdapter.notifyDataSetChanged()
            },
            Response.ErrorListener { error ->

            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode) {
                    "get_nama" -> {
                        hm.put("mode", "get_nama")
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlUser,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nip = jsonObject.getString("nip")
                val jabatan = jsonObject.getString("jabatan")
                val golongan = jsonObject.getString("golongan")
                val telpon = jsonObject.getString("no_hp")

                tvNipCuti.setText(nip)
                tvJabatanCuti.setText(jabatan)
                tvGolonganCuti.setText(golongan)
                tvNohpCuti.setText(telpon)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = java.util.HashMap<String, String>()
                hm.put("nama", tvNamaCuti.text.toString())
                when(mode) {
                    "lihat_nama" -> {
                        hm.put("mode", "lihat_nama")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun addDataCuti(mode : String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if (respon.equals("1")) {
                    Toast.makeText(this, "Berhasil Mengajukan Permohonan Cuti", Toast.LENGTH_LONG)
                        .show()
                    BaseApplication.notificationHelper.showCutiNotif(
                        tvNamaCuti.text.toString(),
                        "Mengajukan ${spJenisCuti.selectedItem} pada Tanggal ${tvTanggalCuti.text}. Dikirim melalui admin."
                    )
                } else {
                    Toast.makeText(this, "Gagal Mengajukan Permohonan Cuti", Toast.LENGTH_LONG)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }) {
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String, String>()
                val nmFile ="EVIDENCE"+ SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(Date())+".jpg"
                when(mode){
                    "insert"->{
                        hm.put("mode","insert")
                        hm.put("nip", tvNipCuti.text.toString())
                        hm.put("jenis_cuti", spJenisCuti.selectedItem.toString())
                        hm.put("alasan_cuti", tvAlasanCuti.text.toString())
                        hm.put("tanggal_cuti", tvTanggalCuti.text.toString())
                        hm.put("selesai_cuti", tvSelesaiCuti.text.toString())
                        hm.put("lama_cuti", txLamaCuti.text.toString())
                        hm.put("alamat_cuti", tvAlamatCuti.text.toString())
                    }
                    "insert_with_photo"->{
                        hm.put("mode","insert_with_photo")
                        hm.put("nip", tvNipCuti.text.toString())
                        hm.put("jenis_cuti", spJenisCuti.selectedItem.toString())
                        hm.put("alasan_cuti", tvAlasanCuti.text.toString())
                        hm.put("tanggal_cuti", tvTanggalCuti.text.toString())
                        hm.put("selesai_cuti", tvSelesaiCuti.text.toString())
                        hm.put("lama_cuti", txLamaCuti.text.toString())
                        hm.put("alamat_cuti", tvAlamatCuti.text.toString())
                        hm.put("image",imStr)
                        hm.put("file",nmFile)
                    }
                }
                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}