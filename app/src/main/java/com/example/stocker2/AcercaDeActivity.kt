package com.example.stocker2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.example.stocker2.databinding.ActivityAcercaDeBinding
import com.example.stocker2.databinding.LayoutInicioSesionBinding
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class AcercaDeActivity : AppCompatActivity() {
    private val db= FirebaseFirestore.getInstance()
    private val myCollection=db.collection("ACERCADE")
    lateinit var binding:ActivityAcercaDeBinding
    private lateinit var btn_atras: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        val idioma:String=idioma()

        myCollection
            .document(idioma)
            .get()
            .addOnSuccessListener {result->
                val texto=result.getString("texto")
                val textView= TextView(this)
                textView.text=texto
                binding.LinearLayout.addView(textView)
            }
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras=findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener{
            finish()
        }
    }
    fun idioma(): String {
        val currentLocale = Locale.getDefault()
        val language = currentLocale.language
        if(language == "es"){
            return "espanol"
        }
        else if(language=="fr"){
            return "frances"
        }
        else if(language=="de"){
            return "aleman"
        }
        else{
            return "ingles"
        }

    }


    private fun crearObjetosDelXml(){
        binding= ActivityAcercaDeBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}