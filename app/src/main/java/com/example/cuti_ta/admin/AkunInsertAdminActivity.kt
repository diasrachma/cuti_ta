package com.example.cuti_ta.admin

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.activity_akun_insert.*
import org.json.JSONObject

class AkunInsertAdminActivity : AppCompatActivity() {

    lateinit var urlClass: UrlClass
    var levelText = ""

    val kerja = arrayOf("-- Pilih unit Kerja --","Ketua/Wakil" , "Kesekretariatan", "Kepaniteraan", "Hakim")
    lateinit var unitAdapter : ArrayAdapter<String>
    var ket = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun_insert)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        unitAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, kerja)
        idSpUnitKerja.adapter = unitAdapter
//        idSpUnitKerja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
//            override fun onItemSelected(
//                parent: AdapterView<*>?,
//                view: View?,
//                position: Int,
//                id: Long
//            ) {
//                if (position == 1){
//                    unitK = "Ketua"
//                } else if (position == 2) {
//                    unitK = "Sekretaris"
//                } else if (position == 3) {
//                    unitK = "Panitera"
//                } else if (position == 4) {
//                    unitK = "Hakim"
//                }
//            }
//
//            override fun onNothingSelected(parent: AdapterView<*>?) {
//                TODO("Not yet implemented")
//            }
//
//        }

        rgLevelInsertAkun.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.lvPegawai -> levelText = "Pegawai"
                R.id.lvlPimpinan -> levelText = "Pimpinan"
            }
        }

        btnSimpanInsertAkun.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim?")
                .setIcon(R.drawable.warning)
                .setMessage("Jika Anda menekan tombol Next maka Akun akan Otomatis Terdaftar, Ingin mendaftarkan Akun?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    insertAkun("insertAkun")
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }
    }

    private fun AlertDialog() {
        AlertDialog.Builder(this)
            .setTitle("Catatan!")
            .setIcon(R.drawable.warning)
            .setMessage("Silahkan melengkapi Profil Pengguna pada Menu Master User")
            .setPositiveButton("Ok", DialogInterface.OnClickListener { dialogInterface, i ->
                super.onBackPressed()
            })
            .show()
    }

    private fun insertAkun(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("1")){
                    Toast.makeText(this,"Berhasil mendaftarkan Akun", Toast.LENGTH_LONG).show()
                    AlertDialog()
                } else if(respon.equals("2")){
                    Toast.makeText(this,"NIP telah digunakan", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this,"Username sudah digunakan", Toast.LENGTH_LONG).show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                if (idSpUnitKerja.selectedItem.toString().equals("Kesekretariatan") && levelText.equals("Pegawai")) {
                    ket = "Staff Sekretaris"
                } else if (idSpUnitKerja.selectedItem.toString().equals("Kesekretariatan") && levelText.equals("Pimpinan")) {
                    ket = "Pimpinan"
                } else if (idSpUnitKerja.selectedItem.toString().equals("Kepaniteraan") && levelText.equals("Pegawai")) {
                    ket = "Staff Panitera"
                } else if (idSpUnitKerja.selectedItem.toString().equals("Kepaniteraan") && levelText.equals("Pimpinan")) {
                    ket = "Pimpinan"
                } else if (idSpUnitKerja.selectedItem.toString().equals("Ketua") && levelText.equals("Pimpinan")) {
                    ket = "Ketua"
                } else if (idSpUnitKerja.selectedItem.toString().equals("Hakim") && levelText.equals("Pimpinan")) {
                    ket = "Staff Wakil Ketua"
                }

                when(mode){
                    "insertAkun" -> {
                        hm.put("mode","insertAkun")
                        hm.put("username",insUsernameAkun.text.toString())
                        hm.put("password",insPasswordAkun.text.toString())
                        hm.put("nama",insNamaAkun.text.toString())
                        hm.put("nip",insNipAkun.text.toString())
                        hm.put("keterangan", ket)
                        hm.put("unit_kerja", idSpUnitKerja.selectedItem.toString())
                        hm.put("level",levelText)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun Back() {
        AlertDialog.Builder(this)
            .setIcon(R.drawable.warning)
            .setTitle("Peringatan!")
            .setMessage("Yakin ingin membatalkan membuat akun?")
            .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                super.onBackPressed()
            })
            .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
            })
            .show()
        true
    }

    override fun onBackPressed() {
        if (!insNamaAkun.text.toString().equals("") || !insNipAkun.text.toString().equals("") || !insUsernameAkun.text.toString().equals("")
            || !insPasswordAkun.text.toString().equals("")) {
            Back()
        } else {
            super.onBackPressed()
        }
    }
}