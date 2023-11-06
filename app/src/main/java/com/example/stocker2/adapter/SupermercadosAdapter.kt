package com.example.stocker2.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocker2.R
import com.example.stocker2.SuperMercado
import com.example.stocker2.databinding.ItemSupermercadoBinding
import com.example.stocker2.databinding.LayoutRegistroBinding

class SupermercadosAdapter: RecyclerView.Adapter<SupermercadosAdapter.SuperMercadoViewHolder>() {
    private lateinit var binding: ItemSupermercadoBinding
    private var supermercados: List<SuperMercado> = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperMercadoViewHolder {

        val layoutInflater= LayoutInflater.from(parent.context).inflate(R.layout.item_supermercado,parent,false)
        return SuperMercadoViewHolder(layoutInflater)
    }
    override fun onBindViewHolder(holder: SuperMercadoViewHolder, position: Int) {
        val SuperMercado = supermercados[position]
        holder.render(SuperMercado)
    }

    override fun getItemCount(): Int {
        return supermercados.size

    }
    inner class SuperMercadoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun render(Super:SuperMercado){
            binding.textViewId.text=Super.id.toString()
            binding.textViewNombre.text=Super.nombre
            binding.textViewTelefono.text=Super.Telefono
            binding.textViewCiudad.text=Super.Ciudad
            binding.textViewPaginaWeb.text=Super.paginaweb
            Glide.with(binding.imageViewProducto.context)
                .load("https://img.freepik.com/vector-premium/icono-carrito-compras-supermercado-aplicaciones-sitios-web_647138-76.jpg")
                .into(binding.imageViewProducto)
        }


    }
    fun setSupermercados(supermercados: List<SuperMercado>) {
        this.supermercados = supermercados
        notifyDataSetChanged()
    }
}

