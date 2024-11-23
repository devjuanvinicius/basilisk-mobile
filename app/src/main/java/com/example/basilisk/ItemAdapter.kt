package com.example.basilisk

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.basilisk.model.Despesas

class ItemAdapter(
    val lista: List<Despesas>
) : Adapter<ItemAdapter.ItemViewHolder>() {

    inner class ItemViewHolder(
        val itemView: View
    ) : ViewHolder(itemView){
        val tituloDespesa: TextView = itemView.findViewById(R.id.tituloDespesa)
        val dataDespesa: TextView = itemView.findViewById(R.id.dataDespesa)
        val valorDespesa: TextView = itemView.findViewById(R.id.valorDespesa)
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

        holder.tituloDespesa.text = lista[position].nome
        holder.dataDespesa.text = lista[position].dataPagamento
        holder.valorDespesa.text = lista[position].valor.toString()
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}