package com.example.stocker2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.stocker2.databinding.ActivityIngresoProductosBinding
import com.example.stocker2.databinding.LayoutRegistroBinding

private lateinit var binding: ActivityIngresoProductosBinding
class ActivityIngresoProductos : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       crearObjetosDelXml()
    }
    private fun crearObjetosDelXml(){
        binding=ActivityIngresoProductosBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}