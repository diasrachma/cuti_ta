package com.example.cuti_ta.admin

import android.content.DialogInterface
import android.content.Intent
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
import kotlinx.android.synthetic.main.activity_akun_edit.*
import org.json.JSONObject
import java.util.HashMap

class AkunEditActivity : AppCompatActivity() {
    lateinit var urlClass: UrlClass

    val kerja = arrayOf("-- Pilih unit Kerja --","Ketua/Wakil" , "Kesekretariatan", "Kepaniteraan", "Hakim")
    lateinit var unitAdapter : ArrayAdapter<String>
    var kj = ""

    var unitK = ""
    var levelText = ""
    var usr = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_akun_edit)
        supportActionBar?.hide()
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()

        unitAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, kerja)
        edtUnitKerja.adapter = unitAdapter
        edtUnitKerja.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 1){
                    unitK = "Ketua"
                }
                else if (position == 2) {
                    unitK = "Sekretaris"
                } else if (position == 3) {
                    unitK = "Panitera"
                } else if (position == 4) {
                    unitK = "Hakim"
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        rgLevel.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.lvPegawaiEdit -> levelText = "Pegawai"
                R.id.lvlPimpinanEdit -> levelText = "Pimpinan"
            }
        }

        btnSimpanEditAkun.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Kirim?")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah Anda yakin ingin mengedit akun ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    editAkun("editAkun")
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }
    }

    override fun onStart() {
        super.onStart()
        showDetail("detailAkun")
    }

    private fun showDetail(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val username = jsonObject.getString("username")
                val unitKerja = jsonObject.getString("unit_kerja")

                usr = username
                edtUsernameAkun.setText(username)
                val spinnerPosition = unitAdapter.getPosition(unitKerja)
                edtUnitKerja.setSelection(spinnerPosition)
            },
            Response.ErrorListener { error ->
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                var paket : Bundle? = intent.extras
                when(mode){
                    "detailAkun" -> {
                        hm.put("mode","detailAkun")
                        hm.put("username", paket?.getString("username").toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun editAkun(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlAkun,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")

                if(respon.equals("0")){
                    Toast.makeText(this,"Password lama yang anda masukkan salah", Toast.LENGTH_LONG).show()
                }else if(respon.equals("1")){
                    Toast.makeText(this,"Berhasil mengedit Akun", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AkunAdminActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "editAkun" -> {
                        hm.put("mode","editAkun")
                        hm.put("usernameLama", usr)
                        hm.put("username", edtUsernameAkun.text.toString())
                        hm.put("password", edtPasswordBaru.text.toString())
                        hm.put("passwordLama", edtPasswordLama.text.toString())
                        hm.put("level", levelText)
                        hm.put("unit_kerja", unitK)
                    }
                }

                return hm
            }
        }
        val  queue = Volley.newRequestQueue(this)
        queue.add(request)
    }
}