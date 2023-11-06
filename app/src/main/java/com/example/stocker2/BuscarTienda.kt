package com.example.stocker2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker2.adapter.SupermercadosAdapter
import com.example.stocker2.databinding.ActivityBuscarTiendaBinding
import com.example.stocker2.databinding.ActivityProductosMercadoBinding
import com.example.stocker2.databinding.LayoutInicioSesionBinding
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class BuscarTienda : AppCompatActivity(),SupermercadosAdapter.OnItemClickListener {
    private lateinit var binding: ActivityBuscarTiendaBinding
    private val db = FirebaseFirestore.getInstance()
    private val myCollection = db.collection("supermercados")
    private lateinit var btn_atras: ImageView
    private lateinit var adapter: SupermercadosAdapter
    val manager = LinearLayoutManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        crearObjetosDelXml()
        initRecicleView()
        GlobalScope.launch {
            delay(1000)
            cargarDatosDesdeFirestore()
        }
        setSupportActionBar(binding.appbar.toolb)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        btn_atras=findViewById(R.id.btn_atras)
        btn_atras.setOnClickListener{
            finish()
        }

    }

    private fun crearObjetosDelXml() {
        binding = ActivityBuscarTiendaBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initRecicleView() {
        val decoration = DividerItemDecoration(this, manager.orientation)
        binding.recyclerMercados.layoutManager = manager
        adapter = SupermercadosAdapter()
        binding.recyclerMercados.adapter = adapter
        binding.recyclerMercados.addItemDecoration(decoration)
        adapter.setOnItemClickListener(this)
    }

    private fun cargarDatosDesdeFirestore() {
        myCollection
            .get()
            .addOnSuccessListener { querySnapshot ->
                val supermercados = ArrayList<SuperMercado>()
                for (document in querySnapshot) {
                    val nombreSupermercado = document.id // Nombre del documento en Firestore
                    var supermercado = document.toObject(SuperMercado::class.java)
                    supermercado.nombre = nombreSupermercado // Asignar el nombre del supermercado
                    supermercados.add(supermercado)
                }
                Log.d("FirestoreData", "Supermercados: $supermercados")
                adapter.setSupermercados(supermercados)
            }
            .addOnFailureListener { e ->
                Log.e("FirestoreData", "Error al obtener datos de Firestore: $e")
            }
    }
    override fun onItemClick(supermercado: SuperMercado) {
        val intent= Intent(this,ProductosMercado::class.java)
        intent.putExtra("NombreEmpresa",supermercado.nombre)
        startActivity(intent)
    }

}





