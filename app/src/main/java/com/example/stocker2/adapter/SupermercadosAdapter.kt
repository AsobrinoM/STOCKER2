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
    private var onItemClickListener: OnItemClickListener? = null
    private var filtroCiudad:String=""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperMercadoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSupermercadoBinding.inflate(layoutInflater, parent, false)
        return SuperMercadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuperMercadoViewHolder, position: Int) {

        val SuperMercado = supermercados[position]
        if (filtroCiudad=="") {


            Log.d("controlfiltro", "Aqui el filtro esta vacio")
            holder.render(SuperMercado)
        }
        else{
            val superciudad= SuperMercado.Ciudad.lowercase(Locale.ROOT)
            Log.d("controlfiltro", "Aqui el filtro tiene $filtroCiudad")
            if(superciudad==filtroCiudad){
                Log.d("controlfiltro", "Aqui el filtro ha dejado pasar un super $SuperMercado")
                    holder.render(SuperMercado)
            }
            else {
                GlobalScope.launch(Dispatchers.Main) {
                    supermercados.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, supermercados.size)
                }
            }
        }
        }

    override fun getItemCount(): Int {
        return supermercados.size
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
            binding.textViewCE.text = Super.correo
            binding.textViewCiudad.text = Super.Ciudad
            Glide.with(binding.imageViewProducto.context)
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS5GuV8OQCtWOlFIAOGTqpeKXuGUwFYHin5yA&usqp=CAU")
                .into(binding.imageViewProducto)
        }
    }

    fun setSupermercados(supermercados: MutableList<SuperMercado>) {
        this.supermercados = supermercados
        notifyDataSetChanged()
    }
    fun setFiltroCiudad(ciudad:String?) {
        if (ciudad != null) {
            this.filtroCiudad= ciudad.lowercase(Locale.ROOT)
        }
        else{
            Log.d("controlfiltro", " ESTA EN NULL EL FILTRO DE CIUDAD,raro ")
        }
    }

    interface OnItemClickListener {
        fun onItemClick(supermercado: SuperMercado)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}