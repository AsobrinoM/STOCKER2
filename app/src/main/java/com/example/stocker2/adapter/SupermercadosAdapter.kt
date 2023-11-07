package com.example.stocker2.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.stocker2.SuperMercado
import com.example.stocker2.databinding.ItemSupermercadoBinding

class SupermercadosAdapter : RecyclerView.Adapter<SupermercadosAdapter.SuperMercadoViewHolder>() {

    private lateinit var binding: ItemSupermercadoBinding
    private var supermercados: List<SuperMercado> = emptyList()
    private var onItemClickListener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SuperMercadoViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemSupermercadoBinding.inflate(layoutInflater, parent, false)
        return SuperMercadoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SuperMercadoViewHolder, position: Int) {
        val SuperMercado = supermercados[position]
        holder.render(SuperMercado)
    }

    override fun getItemCount(): Int {
        return supermercados.size
    }

    inner class SuperMercadoViewHolder(private val binding: ItemSupermercadoBinding) :
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
            binding.textViewCorreo.text = Super.Correo
            binding.textViewCiudad.text = Super.Ciudad
            Glide.with(binding.imageViewProducto.context)
                .load("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcS5GuV8OQCtWOlFIAOGTqpeKXuGUwFYHin5yA&usqp=CAU")
                .into(binding.imageViewProducto)
        }
    }

    fun setSupermercados(supermercados: List<SuperMercado>) {
        this.supermercados = supermercados
        notifyDataSetChanged()
    }

    interface OnItemClickListener {
        fun onItemClick(supermercado: SuperMercado)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        onItemClickListener = listener
    }
}