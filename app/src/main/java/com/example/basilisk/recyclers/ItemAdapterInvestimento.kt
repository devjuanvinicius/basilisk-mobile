package com.example.basilisk.recyclers

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.basilisk.R
import com.example.basilisk.model.Investimento

class ItemAdapterInvestimento(
    val lista: List<Investimento>
) : Adapter<ItemAdapterInvestimento.ItemViewHolder>() {

    inner class ItemViewHolder(
        val itemView: View
    ) : ViewHolder(itemView){
        val titulo: TextView = itemView.findViewById(R.id.tituloItemLista)
        val codigo: TextView = itemView.findViewById(R.id.subTituloItemLista)
        val valor: TextView = itemView.findViewById(R.id.valorItemLista)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        val itemView = layoutInflater.inflate(
            R.layout.item_lista, parent, false
        )

        return ItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val itemDaLista = lista[position]

        holder.titulo.text = itemDaLista.dataCompra
        holder.codigo.text = itemDaLista.codigoAcao
        holder.valor.text = itemDaLista.valor.toString()
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}