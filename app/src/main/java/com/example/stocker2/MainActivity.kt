package com.example.stocker2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.stocker2.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXML()


    }
    private fun crearObjetosDelXML(){
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }


    fun abrirRegistrar(view: View){
        val intent= Intent(this,ActividadRegistro::class.java)
        startActivity(intent)
    }

    fun abrirInicioSec(view: View){
        val intent= Intent(this,ActividadInicioSesion::class.java)
        startActivity(intent)
    }
    fun abrirBuscarTienda(view: View){
        val intent=Intent(this,BuscarTienda::class.java)
        startActivity(intent)

    }
}