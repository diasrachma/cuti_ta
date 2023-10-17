package com.example.cuti_ta.pimpinan

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.BaseApplication
import com.example.cuti_ta.LoginActivity
import com.example.cuti_ta.PrivasiAkunActivity
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterIzinArsip
import com.example.cuti_ta.admin.TtdAdminActivity
import com.example.cuti_ta.admin.UserAdminActivity
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.DefaultValueFormatter
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_pimpinan.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONArray
import org.json.JSONObject

class MainPimpinanActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""
    val JABATAN = "jabatan"
    val DEF_JABATAN = ""

    var jbt = ""

    val daftarIzin = mutableListOf<HashMap<String,String>>()
    lateinit var izinAdapter : AdapterIzinArsip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_pimpinan)
        supportActionBar?.setTitle("Home Pimpinan")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        izinAdapter = AdapterIzinArsip(daftarIzin)
        rvIzinKeluarMainPimpinan.layoutManager = LinearLayoutManager(this)
        rvIzinKeluarMainPimpinan.adapter = izinAdapter

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayoutPimpinan)
        val navView : NavigationView = findViewById(R.id.nav_viewPimpinan)

        toggle = ActionBarDrawerToggle(this, drawerLayoutPimpinan, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        val jbt = preferences.getString(JABATAN, DEF_JABATAN).toString()
        if (jbt.equals("Ketua") || jbt.equals("Wakil Ketua")) {
            val formPimpinanMenuItem = navView.menu.findItem(R.id.nav_formPimpinan)
            formPimpinanMenuItem.isVisible = false
            val riwayatAnda = navView.menu.findItem(R.id.nav_riwayatPimpinan)
            riwayatAnda.isVisible = false
            val kuotaAnda = navView.menu.findItem(R.id.nav_kuotaPimpinan)
            kuotaAnda.isVisible = false

            cardCutiAnda.visibility = View.GONE
            pieChart.visibility = View.GONE
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_homePimpinan -> {
                    supportActionBar?.setTitle("Home Pimpinan")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_profilPimpinan -> {
                    startActivity(Intent(this, ProfilPimpinanActivity::class.java))
                }
                R.id.nav_userPimpinan -> {
                    val intent = Intent(this, UserAdminActivity::class.java)
                    intent.putExtra("jabatan", jabatanHeader.text.toString())
                    startActivity(intent)
                }
                R.id.nav_formPimpinan -> {
                    val intent = Intent(this, FormPimpinanActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_riwayatPimpinan -> {
                    startActivity(Intent(this, RiwayatPimpinanActivity::class.java))
                }
                R.id.nav_arsipPimpinan -> {
                    startActivity(Intent(this, ArsipPendingPimpinanActivity::class.java))
                }
                R.id.nav_kuotaPimpinan -> {
                    startActivity(Intent(this, KuotaCutiPimpinanActivity::class.java))
                }
                R.id.nav_formIzinKeluarPimpinan -> {
                    startActivity(Intent(this, IzinFormPimpinanActivity::class.java))
                }
                R.id.nav_riwayatIzinKeluarPimpinan -> {
                    startActivity(Intent(this, IzinRiwayatPimpinanActivity::class.java))
                }
                R.id.nav_arsipIzinKeluarPimpinan -> {
                    startActivity(Intent(this, IzinArsipPimpinanActivity::class.java))
                }
                R.id.nav_resetPimpinan -> {
                    startActivity(Intent(this, PrivasiAkunActivity::class.java))
                }
                R.id.nav_logoutPimpinan -> {
                    AlertDialog.Builder(this)
                        .setIcon(R.drawable.warning)
                        .setTitle("Logout")
                        .setMessage("Apakah Anda ingin keluar aplikasi?")
                        .setPositiveButton("Ya", DialogInterface.OnClickListener { dialogInterface, i ->
                            val prefEditor = preferences.edit()
                            prefEditor.putString(USERNAME,null)
                            prefEditor.putString(PASSWORD,null)
                            prefEditor.commit()

                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                            finishAffinity()
                        })
                        .setNegativeButton("Tidak", DialogInterface.OnClickListener { dialogInterface, i ->
                        })
                        .show()
                    true
                }
            }
            true
        }

        showProfil("show_profil")
        showChart("chart_pegawai")
        showDataIzin("read_arsip")
        lihatCutiExpired()
        izinTerlambat()
    }

    fun lihatCutiExpired() {
        val request = object : StringRequest(
            Method.POST,urlClass.urlCuti,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                val nama = jsonObject.getString("nama")
                val id = jsonObject.getString("id_cuti")

//                if (respon.equals("1")) {
//                    BaseApplication.notificationHelper.notifTelat(nama,
//                    "Terdapat tenggat cuti hari ini. Silahkan melakukan validasi cuti dari $nama.", id)
//                } else
                if (respon.equals("1")) {
                    BaseApplication.notificationHelper.notifTelat(nama,
                        "Terdapat cuti yang telah melebihi tenggat waktu. Cuti telah dibatalkan oleh sistem.", id)
                }

            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                hm.put("mode", "notif_cuti_expired")

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    fun izinTerlambat() {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val respon = jsonObject.getString("respon")
                val nama = jsonObject.getString("nama")
                val id = jsonObject.getString("id_izinkeluar")

                if (respon.equals("1")) {
                    BaseApplication.notificationHelper.notifTelatKembali(nama,
                        "Terdapat izin keluar yang melebihi jam kembali. Segera konfirmasi jika Anda telah kembali ke kantor!", id)
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                hm.put("username",preferences.getString(USERNAME, DEF_USERNAME).toString())
                hm.put("password",preferences.getString(PASSWORD, DEF_PASSWORD).toString())
                hm.put("mode", "notif_izin_terlambat")

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showProfil(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlProfil,
            Response.Listener { response ->
                val jsonObject = JSONObject(response)
                val nama = jsonObject.getString("nama")
                val jabatan = jsonObject.getString("jabatan")
                val ket = jsonObject.getString("keterangan")
                val foto = jsonObject.getString("foto")

                if (jabatan.equals("Ketua") || jabatan.equals("Wakil Ketua")) {
                    showCharPimpinan("chart_pimpinan", "")
                } else {
                    if (jabatan.equals("Panitera")) {
                        showCharPimpinan("chart_pimpinan", "Kepaniteraan")
                    } else if (jabatan.equals("Sekretaris")) {
                        showCharPimpinan("chart_pimpinan", "Kesekretariatan")
                    }
                }

                if (jabatan.equals("Ketua") || jabatan.equals("Wakil Ketua")) {
                    ketHeader.visibility = View.GONE
                } else {
                    ketHeader.setText(ket)
                }

                usernameHeader.setText(nama)
                jabatanHeader.setText(jabatan)
                Picasso.get().load(foto).into(profilHeader)
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

    private fun showCharPimpinan(mode: String, nk: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlChart,
            Response.Listener { response ->
                val entries = ArrayList<BarEntry>()
                val jsonArray = JSONArray(response)
                val labels = mutableListOf<String>()
                for (i in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val x = jsonObject.getString("status_cuti")
                    val y = jsonObject.getInt("value")
                    entries.add(BarEntry(i.toFloat(), y.toFloat()))
                    labels.add(x)
                }

                setHorizontalBarChartData(entries, labels)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "chart_pimpinan" -> {
                        hm.put("mode","chart_pimpinan")
                        hm.put("unit_kerja", nk)
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun showDataIzin(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlIzin,
            Response.Listener { response ->
                daftarIzin.clear()
                if (response.equals(0)) {
                    Toast.makeText(this,"Data tidak ditemukan", Toast.LENGTH_LONG).show()
                } else {
                    val jsonArray = JSONArray(response)
                    for (x in 0..(jsonArray.length()-1)){
                        val jsonObject = jsonArray.getJSONObject(x)
                        var  frm = HashMap<String,String>()
                        frm.put("img_profil",jsonObject.getString("img_profil"))
                        frm.put("nama",jsonObject.getString("nama"))
                        frm.put("id_izinkeluar",jsonObject.getString("id_izinkeluar"))
                        frm.put("tgl_izinkeluar",jsonObject.getString("tgl_izinkeluar"))
                        frm.put("jam_izinkeluar",jsonObject.getString("jam_izinkeluar"))

                        daftarIzin.add(frm)
                    }
                    izinAdapter.notifyDataSetChanged()
                }
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "read_arsip" -> {
                        hm.put("mode","read_arsip_pimpinan_main")
                    }
                }

                return hm
            }
        }
        val queue = Volley.newRequestQueue(this)
        queue.add(request)
    }

    private fun setHorizontalBarChartData(entries: List<BarEntry>, labels: List<String>) {
        val dataSet = BarDataSet(entries, "Data")
        dataSet.setColors(*ColorTemplate.JOYFUL_COLORS)

        val barData = BarData(dataSet)
        horizontalBarChart.data = barData
        horizontalBarChart.description.isEnabled = false

        // Mengatur sumbu X dengan label yang sesuai
        val xAxis = horizontalBarChart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        horizontalBarChart.animateX(1000)
        horizontalBarChart.invalidate()
    }

    private fun showChart(mode: String) {
        val request = object : StringRequest(
            Method.POST,urlClass.urlChart,
            Response.Listener { response ->
                val entries = ArrayList<PieEntry>()
                val jsonArray = JSONArray(response)
                for (i in 0..(jsonArray.length()-1)){
                    val jsonObject = jsonArray.getJSONObject(i)
                    val value = jsonObject.getInt("value")
                    val sts = jsonObject.getString("status_cuti")
                    entries.add(PieEntry(value.toFloat(), sts))
                }

                setupPieChart(entries)
            },
            Response.ErrorListener { error ->
                Toast.makeText(this,"Tidak dapat terhubung ke server", Toast.LENGTH_LONG).show()
            }){
            override fun getParams(): MutableMap<String, String>? {
                val hm = HashMap<String,String>()
                when(mode){
                    "chart_pegawai" -> {
                        hm.put("mode","chart_pegawai")
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

    private fun setupPieChart(entries: ArrayList<PieEntry>) {
        val dataSet = PieDataSet(entries, "Data")
        dataSet.setColors(Color.parseColor("#FF9CCC65"), Color.parseColor("#FFEF5350"), Color.parseColor("#FF42A5F5")) // Atur warna slice
        dataSet.setDrawValues(true)
        dataSet.valueFormatter = DefaultValueFormatter(0)

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.description.isEnabled = false // Nonaktifkan deskripsi
        pieChart.setUsePercentValues(false)
        pieChart.legend.isEnabled = false
        pieChart.animateY(1000)
    }

    fun WhatsApp() {
        try {
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "Hello Admin")
                putExtra("jid", "${+6285748053370}@s.whatsapp.net")
                type = "text/plain"
                setPackage("com.whatsapp")
            }
            startActivity(sendIntent)
        }catch (e: Exception){
            e.printStackTrace()
            val appPackageName = "com.whatsapp"
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e :android.content.ActivityNotFoundException) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
        }
    }

    override fun onBackPressed() {
        if (drawerLayoutPimpinan.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutPimpinan.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}