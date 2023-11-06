package com.example.stocker2.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.stocker2.R
import com.example.stocker2.SuperMercado
import com.example.stocker2.databinding.ItemSupermercadoBinding


class SupermercadosAdapter: RecyclerView.Adapter<SupermercadosAdapter.SuperMercadoViewHolder>() {
    private lateinit var binding: ItemSupermercadoBinding
    private var supermercados: List<SuperMercado> = emptyList()
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
    inner class SuperMercadoViewHolder(private val binding: ItemSupermercadoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun render(Super: SuperMercado) {
            binding.textViewId.text = Super.id.toString()
            binding.textViewNombre.text = Super.nombre
            binding.textViewTelefono.text = Super.Telefono
            binding.textViewCiudad.text = Super.Ciudad
            binding.textViewPaginaWeb.text = Super.paginaweb
        }
    }
    fun setSupermercados(supermercados: List<SuperMercado>) {
        this.supermercados = supermercados
        notifyDataSetChanged()
    }
}

