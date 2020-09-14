package com.example.infraccionesapi

import android.Manifest
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.infraccionesapi.permission.PermissionsActivity
import com.example.infraccionesapi.permission.PermissionsChecker
import kotlinx.android.synthetic.main.activity_infracciones.*
import kotlinx.android.synthetic.main.activity_login.*
import org.json.JSONException
import org.json.JSONObject

class Login : AppCompatActivity() {
    var REQUEST_LOCATION = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        pedirPermisos()
        val preferences = getSharedPreferences("variables", Context.MODE_PRIVATE)
        val idt = preferences.getString("sesion", "")

        var checker: PermissionsChecker? = null

        checker = PermissionsChecker(this)


            if (checker!!.lacksPermissions(*PermissionsChecker.REQUIRED_PERMISSION)) {
                PermissionsActivity.startActivityForResult(
                    this,
                    PermissionsActivity.PERMISSION_REQUEST_CODE,
                    *PermissionsChecker.REQUIRED_PERMISSION
                )
            } else {


                if (idt.equals("si")) {

                    if (checker!!.lacksPermissions(*PermissionsChecker.REQUIRED_PERMISSION)) {
                        PermissionsActivity.startActivityForResult(
                            this,
                            PermissionsActivity.PERMISSION_REQUEST_CODE,
                            *PermissionsChecker.REQUIRED_PERMISSION
                        )
                    } else {
                        buttoningresar.isEnabled = false
                        _cellText.setText(preferences.getString("usuario", ""))
                        _passwordText.setText(preferences.getString("contraseña", ""))
                        consultar()
                    }
                }
            }


    }


    fun entrar(v: View?) {

        var nom=_cellText.text.toString()
        var pass=_passwordText.text.toString()

        if(nom==""){
            Toast.makeText(this, "Ingrese usuario", Toast.LENGTH_SHORT).show()

        }

        else if(pass==""){
            Toast.makeText(this, "Ingrese password", Toast.LENGTH_SHORT).show()

        }
        else {
            consultar()
        }

    }


    fun consultar() {

        val progressDialog = ProgressDialog(this,


            R.style.Theme_AppCompat_Light_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Cargando datos...")
        progressDialog.show()

        val nomb = _cellText.text
        val pass = _passwordText.text

        val datos = JSONObject()
        try {
            datos.put("Usuario", nomb)
            datos.put("Contraseña", pass)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requstQueue = Volley.newRequestQueue(this)
        val jsonObjectRequest: JsonObjectRequest = @RequiresApi(Build.VERSION_CODES.O)
        object : JsonObjectRequest(
            Method.POST, "https://infracciones.gesdesapplication.com/api/Agentes/Login", datos,
            Response.Listener { response ->
                try {
                    progressDialog?.dismiss()
                    val result = response["resultado"] as Int
                    if (result == 1) {
                            val preferencias = this.getSharedPreferences("variables", Context.MODE_PRIVATE)

                        val editor = preferencias.edit()

                        val guias = response.getJSONObject("datos")
                        val pk = guias.getInt("id")
                        val usuario = guias.getString("usuario")
                        val nombre = guias.getString("nombre")
                        val apellidos = guias.getString("apellidos")
                        val telefono = guias.getString("telefono")
                        val direccion = guias.getString("direccion")
                        val contraseña = guias.getString("contraseña")


                        editor.putInt("pk", pk)
                        editor.putString("usuario", usuario)
                        editor.putString("contraseña", contraseña)
                        editor.putString("nombre", nombre)
                        editor.putString("apellidos", apellidos)
                        editor.putString("telefono", telefono)
                        editor.putString("direccion", direccion)

                        if (checkBox.isChecked) {
                            editor.putString("sesion", "si")
                        }

                        editor.commit()
                        val sendMailIntent = Intent(this, MainActivity::class.java)
                        startActivity(sendMailIntent)
                        buttoningresar.isEnabled=true

                        finish()
                    } else {
                        buttoningresar.isEnabled=true

                        val error = response.getString("mensaje")
                        _ShowAlert("Error", error)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                    buttoningresar.isEnabled=true
                    _ShowAlert("Sin conexion", "")

                }
            },
            Response.ErrorListener { error ->
                progressDialog?.dismiss()
                _ShowAlert("Sin conexion", "")

                Log.e("Rest Response", error.toString())
            }
        ) { //here I want to post data to sever
        }
        val MY_SOCKET_TIMEOUT_MS = 15000
        val maxRetries = 2
        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            MY_SOCKET_TIMEOUT_MS,
            maxRetries,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )
        requstQueue.add(jsonObjectRequest)
    }

    private fun _ShowAlert(title: String, mensaje: String) {
        val alertDialog = AlertDialog.Builder(this).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(mensaje)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "Aceptar"
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

    fun pedirPermisos() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.CAMERA),
                    REQUEST_LOCATION)
            }
        }
    }
}
