package com.example.cuti_ta.admin

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import kotlinx.android.synthetic.main.fragment_kuota_edit.view.*
import org.json.JSONObject

class KuotaEditAdminFragment : DialogFragment() {
    lateinit var thisParent : KuotaAdminActivity
    lateinit var urlClass: UrlClass
    lateinit var v : View

    var kd_kuota = ""

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as KuotaAdminActivity
        urlClass = UrlClass()
        v = inflater.inflate(R.layout.fragment_kuota_edit, container, false)

        val data = arguments
        kd_kuota = data?.get("kode").toString()
        v.edtNamaKuota.setText(data?.get("nama").toString())
        v.edtSisaKuotaCuti.setText(data?.get("sisa").toString())
        v.edtTerpakaiKuota.setText(data?.get("terpakai").toString())

        v.edtTahunKuota.setText("2022")
        v.edtTahunKuota2.setText("2023")

        v.btnCloseEditKuota.setOnClickListener {
            dismiss()
        }

        v.btnKirimEditKuota.setOnClickListener {
            AlertDialog.Builder(thisParent)
                .setTitle("Warning!")
                .setIcon(R.drawable.warning)
                .setMessage("Apakah anda ingin mengedit kuota user ini?")
                .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                    editKuota("updateKuota")
                    val intent = Intent(thisParent, KuotaAdminActivity::class.java)
                    startActivity(intent)
                })
                .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                .show()
        }

        return v
    }

    private fun editKuota(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlSisa,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                if (respon.equals("1")) {
                    Toast.makeText(thisParent,"Sisa Cuti berhasil di update!",
                        Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(thisParent, "Gagal Mengedit Sisa Cuti", Toast.LENGTH_LONG)
                        .show()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(thisParent,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "updateKuota" -> {
                        hm.put("mode","updateKuota")
                        hm.put("kd_kuota",kd_kuota)
                        hm.put("tahun_kuota",v.edtTahunKuota.text.toString()+"-"+v.edtTahunKuota2.text.toString())
                        hm.put("sisa_kuota",v.edtSisaKuotaCuti.text.toString())
                        hm.put("terpakai",v.edtTerpakaiKuota.text.toString())
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(thisParent)
        queue.add(request)
    }
}