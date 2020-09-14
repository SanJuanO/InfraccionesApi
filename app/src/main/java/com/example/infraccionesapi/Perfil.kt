package com.example.infraccionesapi

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_infracciones.*
import kotlinx.android.synthetic.main.activity_perfil.*


class Perfil : Fragment() {
    companion object {
        fun newInstance(): Perfil = Perfil()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.activity_perfil, container, false)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(state: Bundle?)
    {
        super.onActivityCreated(state)
        val preferencias = this.requireActivity().getSharedPreferences("variables", Context.MODE_PRIVATE)

        val nom = preferencias.getString("nombre", "")+" "+preferencias.getString("apellidos", "")

        var role=preferencias.getString("direccion", "").toString()


        var  telefono= preferencias.getString("telefono", "").toString()


      //  var imag = preferencias.getString("imagen", "").toString()

        //val decodedString1 = Base64.decode(imag, Base64.DEFAULT)
        //val decodedByte = BitmapFactory.decodeByteArray(decodedString1, 0, decodedString1.size)
        // val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(resources, decodedByte)
        // roundedBitmapDrawable.isCircular = true
        //fotouser.setImageDrawable(roundedBitmapDrawable)
        nombretext.text=nom
        telefonotext.text=telefono
direcciontext.text=role

        cerrarcesion.setOnClickListener(){

cerrarsesio()

        }

    }
    fun cerrarsesio(){
        val preferencias = this.requireActivity().getSharedPreferences("variables", Context.MODE_PRIVATE)

        val editor = preferencias.edit()
        editor.putString("sesion", "no")
        editor.commit()
        val intent = Intent(requireActivity(), Login::class.java)

        // start your next activity
        startActivity(intent)
        requireActivity().finish();
    }


}
