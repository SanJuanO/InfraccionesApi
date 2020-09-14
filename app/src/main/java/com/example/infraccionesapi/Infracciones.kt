package com.example.infraccionesapi

import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.fragment.app.Fragment
import com.android.volley.DefaultRetryPolicy
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.infraccionesapi.LogUtils.LOGE
import com.example.infraccionesapi.permission.PermissionsActivity
import com.example.infraccionesapi.permission.PermissionsActivity.PERMISSION_REQUEST_CODE
import com.example.infraccionesapi.permission.PermissionsChecker
import com.example.infraccionesapi.permission.PermissionsChecker.REQUIRED_PERMISSION
import com.example.infraccionesapi.utilidades.PrintBitmap
import com.example.infraccionesapi.utilidades.Utilidades
import com.example.infraccionesapi.utilidades.Utilidades.outputStream
import com.itextpdf.text.*
import com.itextpdf.text.pdf.BaseFont
import com.itextpdf.text.pdf.PdfWriter
import com.itextpdf.text.pdf.draw.LineSeparator
import kotlinx.android.synthetic.main.activity_infracciones.*
import org.json.JSONException
import org.json.JSONObject
import java.io.*
import java.text.SimpleDateFormat
import java.util.*


class Infracciones : Fragment() {
    var faltaa =  ArrayList<String>()
    var garantia =  ArrayList<String>()


    var nombreoficial= String()
    var numeroparquimetro= String()
    var tarifa= String()
    var placa= String()
    var evidencia= String()
    var faltao= String()
    var garantiao= String()
    var folio= String()
    var fecha= String()
    var fechar= String()
    var hora= String()
    var marcao= String()
    var tipoo= String()
    var imagenlogo =
        "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"

    var Imgbase64: String? = ""
    var mContext: Context? = null

    var checker: PermissionsChecker? = null
    companion object {
        fun newInstance(): Infracciones = Infracciones()
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.activity_infracciones, container, false)
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(state: Bundle?)
    {
        super.onActivityCreated(state)


        val fech = SimpleDateFormat("yyyy-MM-dd")
         fecha = fech.format(Date())
        afecha.setText(fecha)
        val hor = SimpleDateFormat("HH:mm")
        hora = hor.format(Date())
        ahora.setText(hora)


        val preferences = requireActivity().getSharedPreferences("variables", Context.MODE_PRIVATE)
        val nom = preferences.getString("nombre", "")+preferences.getString("apellidos", "")

        nombreoficia.setText(nom)

        faltaa.add("Seleccione una falta")

        faltaa.add("Invacion de cajon")
        faltaa.add("Exceso de tiempo no pagado")
        faltaa.add("No pagar parquimetro")

        garantia.add("Seleccione una garantia")
        garantia.add("Inmovilizadora")
        garantia.add("Placa")

        mContext = getActivity()?.getApplicationContext()



        checker = PermissionsChecker(activity)

      //  var role=preferencias.getString("role", "").toString()
            val adapter: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, faltaa)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
            falta.setAdapter(adapter)
        val adapter2: ArrayAdapter<String> = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_spinner_dropdown_item, garantia)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_item)
        garantiaa.setAdapter(adapter2)
        fotoubc.setOnClickListener(View.OnClickListener { tomarFoto() })
        button4.setOnClickListener(View.OnClickListener {

            if (checker!!.lacksPermissions(*REQUIRED_PERMISSION)) {
                PermissionsActivity.startActivityForResult(
                    requireActivity(),
                    PERMISSION_REQUEST_CODE,
                    *REQUIRED_PERMISSION
                )
            } else {
                if (Utilidades.bluetoothSocket != null) {
                    validar()
                } else {
                    Log.e(
                        "",
                        "Socket nulo"
                    )

                    Toast.makeText(requireActivity(), "Conecta a una impresora", Toast.LENGTH_SHORT).show()

                }
            }


        })
    }




    fun validar() {

        marcao=  marca.text.toString()
        tipoo=  tipo.text.toString()

      nombreoficial=  nombreoficia.text.toString()
      placa=  plac.text.toString()
      numeroparquimetro=  nparquimetro.text.toString()
      tarifa =  "200"
folio="1233455"
     faltao=  faltaa.get(falta.selectedItemPosition)
      garantiao= garantia.get(garantiaa.selectedItemPosition)
        val hor = SimpleDateFormat("HH:mm")
        hora = hor.format(Date())
        ahora.setText(hora)
        if(numeroparquimetro.equals("")){
            Toast.makeText(mContext, "Escriba el numero del parquimetro", Toast.LENGTH_SHORT).show()

            return;
        }
        if(placa.equals("")){
            Toast.makeText(mContext, "Escriba la placa", Toast.LENGTH_SHORT).show()

            return;
        }
        if(falta.selectedItemPosition==0) {
             Toast.makeText(mContext, "Seleccione una falta", Toast.LENGTH_SHORT).show()

              return;

        }
        if(garantiaa.selectedItemPosition==0){
            Toast.makeText(mContext, "Seleccione una garantia", Toast.LENGTH_SHORT).show()

            return;
        }

        if(marcao.equals("")){
            Toast.makeText(mContext, "Tome la foto de la evidencia", Toast.LENGTH_SHORT).show()

            return;
        }
        if(tipoo.equals("")){
            Toast.makeText(mContext, "Tome la foto de la evidencia", Toast.LENGTH_SHORT).show()

            return;
        }
        val builder = AlertDialog.Builder(requireActivity())

        // Set the alert dialog title
        builder.setTitle("Generar infracción?")

        // Display a message on alert dialog
        builder.setMessage("Estas seguro?")
        // Set a positive button and its click listener on alert dialog
        builder.setPositiveButton("Aceptar") { dialog, which ->
            // Do something when user press the positive button

            consultar()
        }
        // Display a negative button on alert dialog
        builder.setNegativeButton("Cancelar"){dialog,which ->
        }
        builder.show()
    }

    fun tomarFoto() {
        val imageTakeIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (imageTakeIntent.resolveActivity(activity!!.packageManager) != null) {
            startActivityForResult(
                imageTakeIntent,
                1
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // if(resultCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK){
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val extras = data!!.extras
            val imageBitmap = extras!!["data"] as Bitmap?
            val roundedBitmapDrawable =
                RoundedBitmapDrawableFactory.create(resources, imageBitmap)
            roundedBitmapDrawable.isCircular = false
            fotoubc.setImageDrawable(roundedBitmapDrawable)
            val encodedImage = encodeImage(imageBitmap)
            Imgbase64 = encodedImage
        }
    }

    private fun encodeImage(bm: Bitmap?): String {
        val baos = ByteArrayOutputStream()
        bm!!.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }
    fun getByteString(
        str: String,
        bold: Int,
        font: Int,
        widthsize: Int,
        heigthsize: Int
    ): ByteArray? {
        if ((str.length == 0) or (widthsize < 0) or (widthsize > 3) or (heigthsize < 0) or (heigthsize > 3
                    ) or (font < 0) or (font > 1)
        ) return null
        var strData: ByteArray? = null
        strData = try {
            str.toByteArray(charset("iso-8859-1"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }
        val command = ByteArray(strData.size + 9)
        val intToWidth = byteArrayOf(0x00, 0x10, 0x20, 0x30) //
        val intToHeight = byteArrayOf(0x00, 0x01, 0x02, 0x03) //
        command[0] = 27 // caracter ESC para darle comandos a la impresora
        command[1] = 69
        command[2] = bold.toByte()
        command[3] = 27
        command[4] = 77
        command[5] = font.toByte()
        command[6] = 29
        command[7] = 33
        command[8] = (intToWidth[widthsize] + intToHeight[heigthsize]).toByte()
        System.arraycopy(strData, 0, command, 9, strData.size)
        return command
    }

    fun createPdf() {

        if (Utilidades.bluetoothSocket != null) {
            try {
                val texto:String ="\n"+
                "Municipio de Apizaco"+"\n"+
                        "Boleta de Infracción"+"\n"+
                        "Fecha:"+"\n"+fecha+"\n"+
                        "Hora:"+"\n"+hora+"\n"+
                        "Folio No.:"+"\n"+folio+"\n"+

                        "Nombre del Inspector:"+"\n"+nombreoficial+"\n"+
                        "Motivo:"+"\n"+faltao+"\n"+
                        "Garantia:"+"\n"+garantiao+"\n"+
                        "Placa:"+"\n"+placa+"\n"+
                        "Marca:"+"\n"+marcao+"\n"+
                        "Tipo:"+"\n"+tipoo+"\n"+
                        "Numero de Parquimetro:"+"\n"+numeroparquimetro+"\n"+
                        "Total:"+"\n"+"$"+"200.00"+"\n"+
                        "MOTIVO DE LA INFRACCIÓN ARTICULOS:"+"\n"+
                        "1.-LA PRESENTE SE IMPONE CON FUNDAMENTO EN EL DISPUESTO POR EL ARTICULO 21 DE LA CONSTITUCIÓN POLÍTICA DE LOS ESTADOS UNIDOS MEXICANOS, 93 DE LA LOCAL, CONFORME LO ESTABLECIDO EN EL BANDO DE POLICÍA Y GOBIERNO DEL MUNICIPIO DE APIZACO, TLAXCALA Y AL REGLAMENTO DE PARQUÍMETROS DE LA CIUDAD."+"\n"+
                        "2.-DEL REGLAMENTO DE LA LEY DE COMUNICACIONES Y TRANSPORTES EN EL ESTADO DE TLAXCALA EN MATERIA DEL TRASPORTE PRIVADO."+"\n"+
                        "3.-ACUDA CON ESTA BOLETA A LA PRESIDENCIA MUNICIPAL AV. 16 DE SEPTIEMBRE ESQ. CUAUHTÉMOC, OFICINA DE PARQUIMETROS."+"\n"+"\n"


                val fuente = 0
                val negrita = 0
                val ancho = 0
                val alto = 0
                val ANCHO_IMG_58_MM = 384
                val MODE_PRINT_IMG = 0
                // Get the bitmap from assets and display into image view
                // Get the bitmap from assets and display into image view
                val decodedString: ByteArray =
                    Base64.decode(imagenlogo, Base64.DEFAULT)
                val bitmap =
                    BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

                outputStream!!.write(
                    PrintBitmap.POS_PrintBMP(
                        bitmap,
                        ANCHO_IMG_58_MM,
                        MODE_PRINT_IMG
                    )
                )

                Utilidades. outputStream!!.write(
                    getByteString(
                        texto,
                        negrita,
                        fuente,
                        ancho,
                        alto
                    )
                )
                Utilidades.outputStream!!.write("\n\n".toByteArray())


                outputStream!!.write(
                    PrintBitmap.POS_PrintBMP(
                        bitmap,
                        ANCHO_IMG_58_MM,
                        MODE_PRINT_IMG
                    )
                )


                Utilidades. outputStream!!.write(
                    getByteString(
                        texto,
                        negrita,
                        fuente,
                        ancho,
                        alto
                    )
                )
                Utilidades.outputStream!!.write("\n\n".toByteArray())
            } catch (e: IOException) {
                Log.e("",
                    "Error al escribir en el socket"
                )
                Toast.makeText(requireActivity(), "Error al interntar imprimir texto", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
        } else {
            Log.e(
                "",
                "Socket nulo"
            )

        }
    }
    fun createPdf2(dest: String?) {
        if (File(dest).exists()) {
            File(dest).delete()
        }
        try {
            /**
             * Creating Document
             */
            val document = Document()

            // Location to save
            PdfWriter.getInstance(document, FileOutputStream(dest))

            // Open to write
            document.open()

            // Document Settings
            document.pageSize = PageSize.A4
            document.addCreationDate()
            document.addAuthor("Apizaco")
            document.addCreator("Gobierno de apizaco")
            /***
             * Variables for further use....
             */
            val mColorAccent = BaseColor(255, 153, 204, 255)
            val mHeadingFontSize = 13.0f
            val mValueFontSize = 16.0f

            /**
             * How to USE FONT....
             */
            val urName =
                BaseFont.createFont("assets/fonts/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED)

            // LINE SEPARATOR
            val lineSeparator =
                LineSeparator()
            lineSeparator.lineColor = BaseColor(0, 0, 0, 68)

            // Title Order Details...
            // Adding Title....
            val mOrderDetailsTitleFont = Font(
                urName,
                18.0f,
                Font.NORMAL,
                BaseColor.RED
            )
            val mOrderDetailsTitleChunk =
                Chunk("Municipio de Apizaco", mOrderDetailsTitleFont)
            val mOrderDetailsTitleParagraph = Paragraph(mOrderDetailsTitleChunk)
            mOrderDetailsTitleParagraph.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraph)
            val mOrderDetailsTitleChunk2 =
                Chunk("Detalle de  Infraccion", mOrderDetailsTitleFont)
            val mOrderDetailsTitleParagraphw2= Paragraph(mOrderDetailsTitleChunk2)
            mOrderDetailsTitleParagraphw2.alignment = Element.ALIGN_CENTER
            document.add(mOrderDetailsTitleParagraphw2)

            // Fields of Order Details...
            // Adding Chunks for Title and value
            val mOrderIdFont = Font(
                urName,
                mHeadingFontSize,
                Font.NORMAL,
                mColorAccent
            )
            val mOrderIdChunk =
                Chunk("Folio No:", mOrderIdFont)
            val mOrderIdParagraph = Paragraph(mOrderIdChunk)
            document.add(mOrderIdParagraph)
            val mOrderIdValueFont = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderIdValueChunk =
                Chunk(folio, mOrderIdValueFont)
            val mOrderIdValueParagraph = Paragraph(mOrderIdValueChunk)
            document.add(mOrderIdValueParagraph)

            // Adding Line Breakable Space....
            document.add(Paragraph(""))
            // Adding Horizontal Line...
            document.add(Chunk(lineSeparator))
            // Adding Line Breakable Space....
            document.add(Paragraph(""))

            // Fields of Order Details...
            val mOrderDateFont = Font(
                urName,
                mHeadingFontSize,
                Font.NORMAL,
                mColorAccent
            )
            val mOrderDateChunk =
                Chunk("Fecha:", mOrderDateFont)
            val mOrderDateParagraph = Paragraph(mOrderDateChunk)
            document.add(mOrderDateParagraph)
            val mOrderDateValueFont = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderDateValueChunk =
                Chunk(fechar, mOrderDateValueFont)
            val mOrderDateValueParagraph = Paragraph(mOrderDateValueChunk)
            document.add(mOrderDateValueParagraph)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))

            // Fields of Order Details...
            val mOrderAcNameFont = Font(
                urName,
                mHeadingFontSize,
                Font.NORMAL,
                mColorAccent
            )
            val mOrderAcNameChunk0 =
                Chunk("Nombre del oficial:", mOrderAcNameFont)
            val mOrderAcNameParagraph0 = Paragraph(mOrderAcNameChunk0)
            document.add(mOrderAcNameParagraph0)
            val mOrderAcNameValueFont0 = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderAcNameValueChunk0 =
                Chunk(nombreoficial, mOrderAcNameValueFont0)
            val mOrderAcNameValueParagraph0 = Paragraph(mOrderAcNameValueChunk0)
            document.add(mOrderAcNameValueParagraph0)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))

            val mOrderAcNameChunk =
                Chunk("Motivo:", mOrderAcNameFont)
            val mOrderAcNameParagraph = Paragraph(mOrderAcNameChunk)
            document.add(mOrderAcNameParagraph)
            val mOrderAcNameValueFont = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderAcNameValueChunk =
                Chunk(faltao, mOrderAcNameValueFont)
            val mOrderAcNameValueParagraph = Paragraph(mOrderAcNameValueChunk)
            document.add(mOrderAcNameValueParagraph)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))

            val mOrderAcNameChunk2 =
                Chunk("Garantia:", mOrderAcNameFont)
            val mOrderAcNameParagraph2 = Paragraph(mOrderAcNameChunk2)
            document.add(mOrderAcNameParagraph2)
            val mOrderAcNameValueFont2 = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderAcNameValueChunk2 =
                Chunk(garantiao, mOrderAcNameValueFont2)
            val mOrderAcNameValueParagraph2 = Paragraph(mOrderAcNameValueChunk2)
            document.add(mOrderAcNameValueParagraph2)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))

            val mOrderAcNameChunk3 =
                Chunk("Placa:", mOrderAcNameFont)
            val mOrderAcNameParagraph3 = Paragraph(mOrderAcNameChunk3)
            document.add(mOrderAcNameParagraph3)
            val mOrderAcNameValueFont3 = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderAcNameValueChunk3 =
                Chunk(placa, mOrderAcNameValueFont3)
            val mOrderAcNameValueParagraph3 = Paragraph(mOrderAcNameValueChunk3)
            document.add(mOrderAcNameValueParagraph3)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))

            val mOrderAcNameChunk4 =
                Chunk("Numero de Parquimetro:", mOrderAcNameFont)
            val mOrderAcNameParagraph4 = Paragraph(mOrderAcNameChunk4)
            document.add(mOrderAcNameParagraph4)
            val mOrderAcNameValueFont4 = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderAcNameValueChunk4 =
                Chunk(numeroparquimetro, mOrderAcNameValueFont4)
            val mOrderAcNameValueParagraph4 = Paragraph(mOrderAcNameValueChunk4)
            document.add(mOrderAcNameValueParagraph4)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))

            val mOrderAcNameChunk5 =
                Chunk("Costo 2.4 UMAS:", mOrderAcNameFont)
            val mOrderAcNameParagraph5 = Paragraph(mOrderAcNameChunk5)
            document.add(mOrderAcNameParagraph5)
            val mOrderAcNameValueFont5 = Font(
                urName,
                mValueFontSize,
                Font.NORMAL,
                BaseColor.BLACK
            )
            val mOrderAcNameValueChunk5 =
                Chunk("$"+tarifa, mOrderAcNameValueFont5)
            val mOrderAcNameValueParagraph5 = Paragraph(mOrderAcNameValueChunk5)
            document.add(mOrderAcNameValueParagraph5)
            document.add(Paragraph(""))
            document.add(Chunk(lineSeparator))
            document.add(Paragraph(""))
            document.close()
            Toast.makeText(mContext, "Creando", Toast.LENGTH_SHORT).show()
            folio = ""
            fechar = ""
            numeroparquimetro = ""
            garantiao = ""
            placa = ""
            FileUtils.openFile(mContext, File(dest))
        } catch (ie: IOException) {
            LOGE("crear Pdf: Error " + ie.localizedMessage)
            Toast.makeText(mContext, ie.localizedMessage, Toast.LENGTH_SHORT).show()

        } catch (ie: DocumentException) {
            Toast.makeText(mContext, ie.localizedMessage, Toast.LENGTH_SHORT).show()
            LOGE("crear Pdf: Error " + ie.localizedMessage)
        } catch (ae: ActivityNotFoundException) {
            Toast.makeText(mContext, ae.localizedMessage, Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun consultar() {
        val preferences = requireActivity().getSharedPreferences("variables", Context.MODE_PRIVATE)
        val idt = preferences.getInt("pk", 0)!!
        val  t = idt.toInt()
        val progressDialog = ProgressDialog(requireActivity(),


            R.style.Theme_AppCompat_Light_Dialog)
        progressDialog.isIndeterminate = true
        progressDialog.setMessage("Generando infracción...")
        progressDialog.show()

        val fec = fecha+"T"+hora


        val datos = JSONObject()
        try {
            datos.put("Fecha", fec)
            datos.put("IdAgente", t)
            datos.put("NoParquimetro", numeroparquimetro)
            datos.put("Costo", 200.00)
            datos.put("Garantia", garantiao)
            datos.put("Placa", placa)
            datos.put("marca", marcao)
            datos.put("modelo", tipoo)

            datos.put("Evidencia", Imgbase64)
            datos.put("Falta", faltao)


        } catch (e: JSONException) {
            e.printStackTrace()
        }
        val requstQueue = Volley.newRequestQueue(requireActivity())
        val jsonObjectRequest: JsonObjectRequest = @RequiresApi(Build.VERSION_CODES.O)
        object : JsonObjectRequest(
            Method.POST, "https://infracciones.gesdesapplication.com/api/Infracciones/Create", datos,
            Response.Listener { response ->
                try {
                    progressDialog?.dismiss()
                    val result = response["resultado"] as Int
                    if (result == 1) {


                        val guias = response.getJSONObject("datos")
                         folio = guias.getString("folio")
                         fechar = guias.getString("fecha")
                         numeroparquimetro = guias.getString("noParquimetro")
                         garantiao = guias.getString("garantia")
                         placa = guias.getString("placa")


                        createPdf()



                    } else {

                        val error = response.getString("mensaje")
                        Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show()

                        _ShowAlert("Error", error)
                    }
                } catch (e: JSONException) {
                    Toast.makeText(requireActivity(), e.toString(), Toast.LENGTH_SHORT).show()

                    e.printStackTrace()
                }
            },
            Response.ErrorListener { error ->
                progressDialog?.dismiss()
                Toast.makeText(requireActivity(), error.toString(), Toast.LENGTH_SHORT).show()

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
        val alertDialog = AlertDialog.Builder(requireActivity()).create()
        alertDialog.setTitle(title)
        alertDialog.setMessage(mensaje)
        alertDialog.setButton(
            AlertDialog.BUTTON_NEUTRAL, "OK"
        ) { dialog, which ->
            dialog.dismiss()
        }
        alertDialog.show()
    }

}


