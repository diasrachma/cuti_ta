package com.example.cuti_ta.admin

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
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
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.example.cuti_ta.LoginActivity
import com.example.cuti_ta.PrivasiAkunActivity
import com.example.cuti_ta.R
import com.example.cuti_ta.UrlClass
import com.example.cuti_ta.adapter.AdapterIzinArsip
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.google.android.material.navigation.NavigationView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main_admin.*
import kotlinx.android.synthetic.main.nav_header.*
import org.json.JSONArray
import org.json.JSONObject


class MainAdminActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    lateinit var urlClass: UrlClass
    lateinit var preferences: SharedPreferences

    val PREF_NAME = "akun"
    val USERNAME = "username"
    val DEF_USERNAME = ""
    val PASSWORD = "password"
    val DEF_PASSWORD = ""

    val daftarIzin = mutableListOf<HashMap<String,String>>()
    lateinit var izinAdapter : AdapterIzinArsip

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_admin)
        supportActionBar?.setTitle("Home Admin")
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)

        urlClass = UrlClass()
        preferences = getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

        izinAdapter = AdapterIzinArsip(daftarIzin)
        rvIzinKeluarMainAdmin.layoutManager = LinearLayoutManager(this)
        rvIzinKeluarMainAdmin.adapter = izinAdapter

        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayoutAdmin)
        val navView : NavigationView = findViewById(R.id.nav_viewAdmin)

        toggle = ActionBarDrawerToggle(this, drawerLayoutAdmin, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.nav_homeAdmin -> {
                    supportActionBar?.setTitle("Home Admin")
                    drawerLayout.closeDrawer(GravityCompat.START)
                }
                R.id.nav_ttdAdmin -> {
                    val intent = Intent(this, TtdAdminActivity::class.java)
                    intent.putExtra("jabatan", jabatanHeader.text.toString())
                    intent.putExtra("nama", usernameHeader.text.toString())
                    startActivity(intent)
                }
                R.id.nav_akunAdmin -> {
                    val intent = Intent(this, AkunAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_userAdmin -> {
                    val intent = Intent(this, UserAdminActivity::class.java)
                    intent.putExtra("jabatan", jabatanHeader.text.toString())
                    startActivity(intent)
                }
                R.id.nav_kuotaAdmin -> {
                    val intent = Intent(this, KuotaAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_formAdmin -> {
                    val intent = Intent(this, FormAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_riwayatAdmin -> {
                    val intent = Intent(this, RiwayatAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_arsipAdmin -> {
                    val intent = Intent(this, ArsipAdminActivity::class.java)
                    startActivity(intent)
                }
                R.id.nav_riwayatIzinKeluarAdmin -> {
                    startActivity(Intent(this, IzinRiwayatAdminActivity::class.java))
                }
                R.id.nav_resetAdmin -> {
                    startActivity(Intent(this, PrivasiAkunActivity::class.java))
                }
                R.id.nav_logoutAdmin -> {
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
//            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        showChart("chart_pimpinan")
        showDataIzin("read_arsip")
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

    private fun showChart(mode: String) {
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
                        hm.put("mode","chart_admin")
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
        horizontalBarChartAdmin.data = barData
        horizontalBarChartAdmin.description.isEnabled = false

        // Mengatur sumbu X dengan label yang sesuai
        val xAxis = horizontalBarChartAdmin.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.position = com.github.mikephil.charting.components.XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.isGranularityEnabled = true

        horizontalBarChartAdmin.animateX(1000)
        horizontalBarChartAdmin.invalidate()
    }

    override fun onBackPressed() {
        if (drawerLayoutAdmin.isDrawerOpen(GravityCompat.START)) {
            drawerLayoutAdmin.closeDrawer(GravityCompat.START)
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
                val jabatan = jsonObject.getString("jabatan")
                val ket = jsonObject.getString("keterangan")
                val foto = jsonObject.getString("foto")

                ketHeader.visibility = View.GONE
                jabatanHeader.setText(jabatan)
                usernameHeader.setText(nama)
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
}