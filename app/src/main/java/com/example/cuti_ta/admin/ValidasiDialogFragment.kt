package com.example.cuti_ta.admin

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.fragment.app.DialogFragment
import com.example.cuti_ta.R
import kotlinx.android.synthetic.main.activity_cuti_detail.*
import kotlinx.android.synthetic.main.dialog_validasi.view.*

class ValidasiDialogFragment : DialogFragment() {
    lateinit var thisParent: CutiDetailAdminActivity
    lateinit var v : View

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        thisParent = activity as CutiDetailAdminActivity
        v = inflater.inflate(R.layout.dialog_validasi, container, false)

        v.btnNextValidasiKetua.setOnClickListener {
            val intent = Intent(v.context, CutiValidasiAdminActivity::class.java)
            intent.putExtra("idCuti", thisParent.tvDetailIdCutiAdmin.text.toString())
            intent.putExtra("jenis", thisParent.tvDetailCutiJenis.text.toString())
            intent.putExtra("nip", thisParent.tvDetailCutiNip.text.toString())
            intent.putExtra("jbt", "Ketua")
            intent.putExtra("lama", thisParent.tvLama)
            intent.putExtra("username", "196812081994031001")
            intent.putExtra("password", "196812081994031001")
            startActivity(intent)
        }

        v.btnNextValidasiPimpinan.setOnClickListener {
            if (thisParent.tvDetailCutiUnitKerja.text.toString().equals("Kepaniteraan")) {
                val intent = Intent(v.context, CutiValidasiAdminActivity::class.java)
                intent.putExtra("idCuti", thisParent.tvDetailIdCutiAdmin.text.toString())
                intent.putExtra("jenis", thisParent.tvDetailCutiJenis.text.toString())
                intent.putExtra("nip", thisParent.tvDetailCutiNip.text.toString())
                intent.putExtra("jbt", "Panitera")
                intent.putExtra("lama", thisParent.tvLama)
                intent.putExtra("username", "196307031988031003")
                intent.putExtra("password", "196307031988031003")
                startActivity(intent)
            } else {
                val intent = Intent(v.context, CutiValidasiAdminActivity::class.java)
                intent.putExtra("idCuti", thisParent.tvDetailIdCutiAdmin.text.toString())
                intent.putExtra("jenis", thisParent.tvDetailCutiJenis.text.toString())
                intent.putExtra("nip", thisParent.tvDetailCutiNip.text.toString())
                intent.putExtra("jbt", "Sekretaris")
                intent.putExtra("lama", thisParent.tvLama)
                intent.putExtra("username", "197804092009122001")
                intent.putExtra("password", "197804092009122001")
                startActivity(intent)
            }
        }

        return v
    }
}