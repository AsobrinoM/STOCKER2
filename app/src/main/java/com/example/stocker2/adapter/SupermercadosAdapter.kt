package com.example.stocker2.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocker2.SuperMercado
import com.example.stocker2.databinding.ItemSupermercadoBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.Locale

class SupermercadosAdapter : RecyclerView.Adapter<SupermercadosAdapter.SuperMercadoViewHolder>() {

    private lateinit var binding: ItemSupermercadoBinding
    private var supermercados: MutableList<SuperMercado> = mutableListOf()
    private var filteredSupermercados: MutableList<SuperMercado> = mutableListOf()
    private var onItemClickListener: OnItemClickListener? = null
    private var filtroCiudad: String = ""
    private var filtroSucursal: String = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperMercadoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSupermercadoBinding.inflate(layoutInflater, parent, false)
        return SuperMercadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuperMercadoViewHolder, position: Int) {
        val SuperMercado = filteredSupermercados[position]
        holder.render(SuperMercado)
    }

    override fun getItemCount(): Int {
        return filteredSupermercados.size
    }


    inner class SuperMercadoViewHolder(private val binding: ItemSupermercadoBinding):
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    onItemClickListener?.onItemClick(supermercados[position])
                }
            }
        }

        fun render(Super: SuperMercado) {
            binding.textViewNombre.text = Super.nombre
            binding.textViewCE.text = Super.direccion
            binding.textViewCiudad.text = Super.Ciudad
            Glide.with(binding.imageViewProducto.context)
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS5GuV8OQCtWOlFIAOGTqpeKXuGUwFYHin5yA&usqp=CAU")
                .into(binding.imageViewProducto)
        }
    }

    fun setSupermercados(supermercados: MutableList<SuperMercado>) {
        this.supermercados = supermercados
        applyFilters()
    }

    fun setFiltroCiudad(ciudad: String?) {
        if (ciudad != null) {
            this.filtroCiudad = ciudad.lowercase(Locale.ROOT)
        }
        applyFilters()
    }

    fun setFiltroSucursal(Sucursal: String?) {
        if (Sucursal != null) {
            this.filtroSucursal = Sucursal.lowercase(Locale.ROOT)
        }
        applyFilters()
    }

    private fun applyFilters() {
        filteredSupermercados.clear()

        for (supermercado in supermercados) {
            val superciudad = supermercado.Ciudad.lowercase(Locale.ROOT)
            val superNombre = supermercado.nombre.lowercase(Locale.ROOT)

            if ((filtroCiudad.isEmpty() || superciudad == filtroCiudad) &&
                (filtroSucursal.isEmpty() || superNombre == filtroSucursal)
            ) {
                Log.d("controlfiltro", " El filtro es este: filtroCiudad: $filtroCiudad y filtroSucursal: $filtroSucursal")
                Log.d("controlfiltro", " Superañadido:$supermercado")
                filteredSupermercados.add(supermercado)
            }
        }

        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(supermercado: SuperMercado)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}